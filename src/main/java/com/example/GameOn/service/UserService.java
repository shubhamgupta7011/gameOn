package com.example.GameOn.service;

import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    UserRepository userRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public UserService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

//    private static final PasswordEncoder passwordEn = new BCryptPasswordEncoder();

//    public Flux<Users> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
//        Query query = new Query();
//
//        // ✅ Apply dynamic filters
//        if (filters != null && !filters.isEmpty()) {
//            filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
//        }
//
//        // ✅ Sorting
//        if (sortBy != null && !sortBy.isEmpty()) {
//            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//            query.with(Sort.by(direction, sortBy));
//        }
//
//        // ✅ Pagination
//        query.skip((long) page * size).limit(size);
//
//        return mongoTemplate.find(query, Users.class);
//    }

//    public Flux<Users> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
//        Query query = new Query();
//
//        // ✅ Handle age filtering
//        if (filters != null && !filters.isEmpty()) {
//            Integer minAge = null;
//            Integer maxAge = null;
//
//            if (filters.containsKey("minAge")) {
//                minAge = (Integer) filters.remove("minAge");
//            }
//            if (filters.containsKey("maxAge")) {
//                maxAge = (Integer) filters.remove("maxAge");
//            }
//
//            if (minAge != null || maxAge != null) {
//                LocalDate today = LocalDate.now();
//
//                Date minDateOfBirth = null;
//                Date maxDateOfBirth = null;
//
//                if (minAge != null) {
//                    maxDateOfBirth = Date.from(today.minusYears(minAge).atStartOfDay(ZoneId.systemDefault()).toInstant());
//                }
//                if (maxAge != null) {
//                    minDateOfBirth = Date.from(today.minusYears(maxAge + 1).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
//                }
//
//                Criteria ageCriteria = Criteria.where("userDetails.personalDetails.dateOfBirth");
//
//                if (minDateOfBirth != null && maxDateOfBirth != null) {
//                    ageCriteria = ageCriteria.gte(minDateOfBirth).lte(maxDateOfBirth);
//                } else if (minDateOfBirth != null) {
//                    ageCriteria = ageCriteria.gte(minDateOfBirth);
//                } else if (maxDateOfBirth != null) {
//                    ageCriteria = ageCriteria.lte(maxDateOfBirth);
//                }
//
//                query.addCriteria(ageCriteria);
//            }
//
//            // ✅ Apply remaining filters
//            filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
//        }
//
//        // ✅ Sorting
//        if (sortBy != null && !sortBy.isEmpty()) {
//            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//            query.with(Sort.by(direction, sortBy));
//        }
//
//        // ✅ Pagination
//        query.skip((long) page * size).limit(size);
//
//        return mongoTemplate.find(query, Users.class);
//    }

    public Flux<UserProfile> getFilteredList(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        Query query = new Query();

        // ✅ Handle age filtering
        if (filters != null && !filters.isEmpty()) {
            Integer minAge = null;
            Integer maxAge = null;

            if (filters.containsKey("minAge")) {
                minAge = (Integer) filters.remove("minAge");
            }
            if (filters.containsKey("maxAge")) {
                maxAge = (Integer) filters.remove("maxAge");
            }

            if (minAge != null || maxAge != null) {
                LocalDate today = LocalDate.now();

                Date minDateOfBirth = null;
                Date maxDateOfBirth = null;

                if (minAge != null) {
                    maxDateOfBirth = Date.from(today.minusYears(minAge).atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
                if (maxAge != null) {
                    minDateOfBirth = Date.from(today.minusYears(maxAge + 1).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                }

                Criteria ageCriteria = Criteria.where("userDetails.personalDetails.dateOfBirth");

                if (minDateOfBirth != null && maxDateOfBirth != null) {
                    ageCriteria = ageCriteria.gte(minDateOfBirth).lte(maxDateOfBirth);
                } else if (minDateOfBirth != null) {
                    ageCriteria = ageCriteria.gte(minDateOfBirth);
                } else if (maxDateOfBirth != null) {
                    ageCriteria = ageCriteria.lte(maxDateOfBirth);
                }

                query.addCriteria(ageCriteria);
            }

            // ✅ Handle geospatial filter
            if (filters.containsKey("latitude") && filters.containsKey("longitude") && filters.containsKey("distanceInKm")) {
                Double latitude = (Double) filters.remove("latitude");
                Double longitude = (Double) filters.remove("longitude");
                Double distanceInKm = (Double) filters.remove("distanceInKm");
                double radians = distanceInKm / 6378.1;

                Point location = new Point(longitude, latitude);

                query.addCriteria(Criteria.where("userDetails.personalDetails.location")
                        .nearSphere(location)
                        .maxDistance(radians));
            }

            // ✅ Apply remaining filters
            filters.forEach((key, value) -> query.addCriteria(Criteria.where(key).is(value)));
        }

        // ✅ Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy));
        }

        // ✅ Pagination
        query.skip((long) page * size).limit(size);

        return mongoTemplate.find(query, UserProfile.class);
    }


    public Mono<UserProfile> saveUser(UserProfile user){
        user.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return userRepository.save(user);
    }

    public Mono<UserProfile> findByUserId(String userId){
        return userRepository.findByUid(userId);
    }

//    public Page<Users> getAllUsers(Pageable pageable) {
//        return userRepository.findAll(pageable);
//    }

    public Mono<UserProfile> saveNewUser(UserProfile user){
//        user.setPassword(passwordEn.encode(user.getPassword()));
        user.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        user.setLastUpdatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant(ZoneId.of("UTC").getRules().getOffset(Instant.now())).toEpochMilli());
        return userRepository.save(user);
    }

    public Flux<UserProfile> getAll(){
        return userRepository.findAll();
    }

    public Mono<UserProfile> getById(ObjectId id){
        return userRepository.findById(id);
    }

    public void delete(ObjectId id){
        userRepository.deleteById(id);
    }

//    public Mono<Users> findByUserName(String username){
//        return userRepository.findByUserName(username);
//    }

    public String generateHashFromPhone(String phone) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phone.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).substring(0, 16); // Trim to 16 chars
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating user ID", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public Mono<UserProfile> updateUserLocation(String userId, double lat, double lon) {
        return userRepository.findByUid(userId)
                .flatMap(user -> {
                    user.getLocation().setLongitude(lon);
                    user.getLocation().setLatitude(lat);
                    return userRepository.save(user);
                });
    }
}

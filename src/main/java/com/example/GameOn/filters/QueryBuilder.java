package com.example.GameOn.filters;

import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QueryBuilder {


    public static <T> Query buildQuery(Map<String, Object> filters, int page, int size, String sortBy, String sortOrder) {
        Query query = new Query();

        // ✅ Age Filtering
        applyAgeFilter(query, filters);

        // ✅ Apply Range-Based Filters (minX, maxX)
        if ((!filters.containsKey("minAge")) && (!filters.containsKey("maxAge")))
            applyRangeFilters(query, filters);

        // ✅ Geospatial Filtering
        applyGeospatialFilter(query, filters);

        // ✅ Remaining Filters
        applyExactAndListFilters(query, filters);

        // ✅ Sorting Logic
        applySorting(query, sortBy, sortOrder);

        // ✅ Pagination Logic
        applyPagination(query, page, size);

        return query;
    }

    // 🔹🔹 Apply Exact Match and "IN" Queries for Lists
    private static void applyExactAndListFilters(Query query, Map<String, Object> filters) {
        filters.forEach((key, value) -> {
            if (value instanceof List) {
                query.addCriteria(Criteria.where(key).in((List<?>) value));
            } else {
                query.addCriteria(Criteria.where(key).is(value));
            }
        });
    }

    // 🔹🔹 Apply Location-Based Filters like maxDistance nearby
    private static void applyGeospatialFilter(Query query, Map<String, Object> filters) {
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
    }

    // 🔹🔹 Apply Range-Based Filters like minPrice, maxPrice, minAge, maxAge
    private static void applyRangeFilters(Query query, Map<String, Object> filters) {
        String[] rangeKeys = {"min", "max"};

        filters.keySet().removeIf(key -> {
            for (String prefix : rangeKeys) {
                if (key.startsWith(prefix)) {
                    String fieldName = key.substring(prefix.length());
                    Object value = filters.get(key);

                    if (value instanceof Number) {
                        if ("min".equals(prefix)) {
                            query.addCriteria(Criteria.where(fieldName).gte(value));
                        } else if ("max".equals(prefix)) {
                            query.addCriteria(Criteria.where(fieldName).lte(value));
                        }
                    }
                    return true; // Remove from filters after processing
                }
            }
            return false;
        });
    }

    // 🔹🔹 Apply Range-Based Filters like minAge, maxAge
    private static void applyAgeFilter(Query query, Map<String, Object> filters) {
        Integer minAge = (Integer) filters.remove("minAge");
        Integer maxAge = (Integer) filters.remove("maxAge");

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
            } else {
                ageCriteria = ageCriteria.lte(maxDateOfBirth);
            }

            query.addCriteria(ageCriteria);
        }
    }

    // 🔹🔹 Apply Sorting
    private static void applySorting(Query query, String sortBy, String sortOrder) {
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
            query.with(Sort.by(direction, sortBy));
        }
    }

    // 🔹🔹 Apply Pagination
    private static void applyPagination(Query query, int page, int size) {
        query.skip((long) page * size).limit(size);
    }
}

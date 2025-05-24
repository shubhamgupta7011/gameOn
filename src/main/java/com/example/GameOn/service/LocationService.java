package com.example.GameOn.service;

import com.example.GameOn.entity.UserDetails.UserProfile;
import com.example.GameOn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class LocationService {

    @Autowired
    private UserRepository userRepo;

    public Flux<UserProfile> findNearbyUsers(double lat, double lon, double radiusKm) {
        long maxDistanceMeters = (long) (radiusKm * 1000);
        return userRepo.findNearby(lat, lon, maxDistanceMeters);
    }

}


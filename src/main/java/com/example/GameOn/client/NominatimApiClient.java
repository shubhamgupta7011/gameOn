package com.example.GameOn.client;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("https://nominatim.openstreetmap.org")

public interface NominatimApiClient {

    @GetExchange("/reverse?format=jsonv2&lat={lat}&lon={lon}")
    Mono<String> getLocationDetails(@PathVariable("lat") Double lat, @PathVariable("lon") Double lon);
}

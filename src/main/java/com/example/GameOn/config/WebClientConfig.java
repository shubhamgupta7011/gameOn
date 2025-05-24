package com.example.GameOn.config;

import com.example.GameOn.client.NominatimApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public NominatimApiClient nominatimApiClient() {
        WebClient client = WebClient.builder()
                .filter((request, next) -> {
                    log.info("🌐 [Request] " + request.method() + " " + request.url());
                    request.headers().forEach((name, values) ->
                            values.forEach(value -> log.info(name + ": " + value)));
                    return next.exchange(request)
                            .doOnNext(response -> {
                                log.info("🔄 [Response Status] " + response.statusCode());
                                response.headers().asHttpHeaders().forEach((name, values) ->
                                        values.forEach(value -> log.info(name + ": " + value)));
                            });
                })
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(NominatimApiClient.class);
    }
}


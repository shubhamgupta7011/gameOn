package com.example.GameOn.config;

import com.example.GameOn.client.NominatimApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public NominatimApiClient nominatimApiClient() {
        WebClient client = WebClient.builder()
                .filter((request, next) -> {
                    System.out.println("🌐 [Request] " + request.method() + " " + request.url());
                    request.headers().forEach((name, values) ->
                            values.forEach(value -> System.out.println(name + ": " + value)));
                    return next.exchange(request)
                            .doOnNext(response -> {
                                System.out.println("🔄 [Response Status] " + response.statusCode());
                                response.headers().asHttpHeaders().forEach((name, values) ->
                                        values.forEach(value -> System.out.println(name + ": " + value)));
                            });
                })
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(NominatimApiClient.class);
    }
}


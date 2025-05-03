package com.example.GameOn.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@Configuration
@EnableReactiveCassandraRepositories
public class CassandraConfig extends AbstractReactiveCassandraConfiguration {

    @Bean
    public CqlSessionBuilderCustomizer cqlSessionBuilderCustomizer() {
        return builder -> builder.withLocalDatacenter("datacenter1");
    }

    @Override
    protected String getKeyspaceName() {
        return "gameon_keyspace";
    }

    @Override
    protected String getContactPoints() {
        return "127.0.0.1";
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }
}

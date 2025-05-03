package com.example.GameOn.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
@Data
public class MatchKey {

    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "matched_id", type = PrimaryKeyType.CLUSTERED)
    private String matchedUserId;

    public MatchKey() {}

    public MatchKey(String userId, String matchedUserId) {
        this.userId = userId;
        this.matchedUserId = matchedUserId;
    }
}


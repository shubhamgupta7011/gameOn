package com.example.GameOn.entity;

import com.example.GameOn.enums.SwipeType;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;

@Data
@Table("matches_by_user")
public class Matches {
    @PrimaryKey
    private MatchKey key;
    private String timestamps;

    public Matches() {}

    public Matches(String userId, String matchedUserId, String timestamps) {
        this.key = new MatchKey(userId, matchedUserId);
        this.timestamps = timestamps;
    }
}


package com.example.GameOn.entity;

import com.example.GameOn.enums.SwipeType;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Data
@Table("swipes_by_user")
public class SwipeByUser {
    @Data
    @PrimaryKeyClass
    public static class Key implements Serializable {
        @PrimaryKeyColumn(name = "swiper_id", type = PrimaryKeyType.PARTITIONED)
        private String swiperId;

        @PrimaryKeyColumn(name = "swipee_id", type = PrimaryKeyType.CLUSTERED)
        private String swipeeId;

        public Key(String swiperId, String swipeeId) {
            this.swiperId = swiperId;
            this.swipeeId = swipeeId;
        }
    }

    @PrimaryKey
    private Key key;

    private SwipeType swipe_type;
    private String timestamp;
}


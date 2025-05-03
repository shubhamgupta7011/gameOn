package com.example.GameOn.entity;

import com.example.GameOn.enums.SwipeType;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;

@Data
@Table("swipes_by_swipee")
public class SwipeBySwipee {
    @Data
    @PrimaryKeyClass
    public static class Key implements Serializable {
        @PrimaryKeyColumn(name = "swipee_id", type = PrimaryKeyType.PARTITIONED)
        private String swipeeId;

        @PrimaryKeyColumn(name = "swiper_id", type = PrimaryKeyType.CLUSTERED)
        private String swiperId;

        public Key(String swiperId, String swipeeId) {
            this.swiperId = swiperId;
            this.swipeeId = swipeeId;
        }
    }

    @PrimaryKey
    private Key key;
    @Column("swipe_type")
    private SwipeType swipeType;
    private String timestamp;
}


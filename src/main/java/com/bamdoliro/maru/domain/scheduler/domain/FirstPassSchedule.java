package com.bamdoliro.maru.domain.scheduler.domain;

import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_schedule")
@Entity
public class FirstPassSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime scheduledTime;

    public FirstPassSchedule(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

}

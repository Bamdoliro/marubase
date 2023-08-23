package com.bamdoliro.maru.domain.fair.domain;

import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_fair")
@Entity
public class Fair extends BaseTimeEntity {

    @Column(name = "fair_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 30)
    private String place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FairType type;

    @Builder
    public Fair(LocalDateTime start, Integer capacity, String place, FairType type) {
        this.start = start;
        this.capacity = capacity;
        this.place = place;
        this.type = type;
    }
}

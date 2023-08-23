package com.bamdoliro.maru.domain.fair.domain;

import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_attendee")
@Entity
public class Attendee extends BaseTimeEntity {

    @Column(name = "attendee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 30)
    private String schoolName;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer headcount;

    @Column(nullable = true, length = 255)
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fair_id")
    private Fair fair;

    @Builder
    public Attendee(String schoolName, String name, String type, String phoneNumber, Integer headcount, String question, Fair fair) {
        this.schoolName = schoolName;
        this.name = name;
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.headcount = headcount;
        this.question = question;
        this.fair = fair;
    }
}

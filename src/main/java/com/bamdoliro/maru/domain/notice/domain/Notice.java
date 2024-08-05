package com.bamdoliro.maru.domain.notice.domain;

import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_notice")
@Entity
public class Notice extends BaseTimeEntity {

    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;

    @Column(unique = true, nullable = true)
    private UUID fileUuid;

    @Builder
    public Notice(String title, String content, UUID fileUuid) {
        this.title = title;
        this.content = content;
        this.fileUuid = fileUuid;
    }

    public void update(String title, String content, UUID fileUuid) {
        this.title = title;
        this.content = content;
        this.fileUuid = fileUuid;
    }
}

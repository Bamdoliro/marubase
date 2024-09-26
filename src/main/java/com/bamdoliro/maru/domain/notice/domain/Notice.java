package com.bamdoliro.maru.domain.notice.domain;

import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_notice_file",
            joinColumns = @JoinColumn(name = "notice_id"))
    private List<String> fileNameList;

    @Builder
    public Notice(String title, String content, List<String> fileNameList) {
        this.title = title;
        this.content = content;
        this.fileNameList = fileNameList;
    }

    public void update(String title, String content, List<String> fileName) {
        this.title = title;
        this.content = content;
        this.fileNameList = fileName;
    }
}

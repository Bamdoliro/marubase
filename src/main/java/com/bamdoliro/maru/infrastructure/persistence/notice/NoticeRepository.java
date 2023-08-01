package com.bamdoliro.maru.infrastructure.persistence.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoticeRepository extends CrudRepository<Notice, Long> {

    List<Notice> findAll();
}

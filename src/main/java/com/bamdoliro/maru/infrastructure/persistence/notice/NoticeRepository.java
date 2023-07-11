package com.bamdoliro.maru.infrastructure.persistence.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import org.springframework.data.repository.CrudRepository;

public interface NoticeRepository extends CrudRepository<Notice, Long> {
}

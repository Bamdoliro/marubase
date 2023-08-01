package com.bamdoliro.maru.domain.notice.exception;

import com.bamdoliro.maru.domain.notice.exception.error.NoticeErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class NoticeNotFoundException extends MaruException {

    public NoticeNotFoundException() {
        super(NoticeErrorProperty.NOTICE_NOT_FOUND);
    }
}

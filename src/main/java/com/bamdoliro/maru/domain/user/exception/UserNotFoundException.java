package com.bamdoliro.maru.domain.user.exception;

import com.bamdoliro.maru.domain.user.exception.error.UserErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class UserNotFoundException extends MaruException {

    public UserNotFoundException() {
        super(UserErrorProperty.USER_NOT_FOUND);
    }
}

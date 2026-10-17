package com.bamdoliro.maru.shared.ratelimit;

import com.bamdoliro.maru.shared.error.MaruException;

public class RateLimitExceededException extends MaruException {

    public RateLimitExceededException() {
        super(RateLimitErrorProperty.TOO_MANY_REQUESTS);
    }
}

package com.qburst.blog_application.exception.base;

public enum JwtErrorType {
    EXPIRED,
    MALFORMED,
    UNSUPPORTED,
    SIGNATURE_INVALID,
    EMPTY,
    BLACKLISTED
}

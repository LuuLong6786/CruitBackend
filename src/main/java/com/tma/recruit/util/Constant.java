package com.tma.recruit.util;

import java.time.Duration;

public class Constant {

    public static final String AUTHENTICATION_HEADER = "Authorization";

    public static final long PASSWORD_RESET_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(10).toMillis();

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofHours(4).toMillis();
}

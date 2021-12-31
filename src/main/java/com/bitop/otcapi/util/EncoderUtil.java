package com.bitop.otcapi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderUtil {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    /**
     * 加密密码
     * @param rawPassword 明文
     * @return
     */
    public static String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }
}

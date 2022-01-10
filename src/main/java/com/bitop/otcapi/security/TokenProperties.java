package com.bitop.otcapi.security;

import lombok.Data;

@Data
public class TokenProperties {
    private  String header;
    private  String secret;
    private  int expireTime;
}

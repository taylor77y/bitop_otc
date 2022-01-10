package com.bitop.otcapi.security;

import java.io.Serializable;

public class JWTInfo implements Serializable,IJWTInfo {
    private static final long serialVersionUID = -51465416161616L;


    private String userName;
    private String userId;
    private String userType;

    public JWTInfo() {
    }

    public JWTInfo(String userName, String userId, String userType) {
        this.userName = userName;
        this.userId = userId;
        this.userType = userType;
    }



    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JWTInfo jwtInfo = (JWTInfo) o;

        if (userName != null ? !userName.equals(jwtInfo.userName) : jwtInfo.userName != null) {
            return false;
        }
        return userId != null ? userId.equals(jwtInfo.userId) : jwtInfo.userId == null;

    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}

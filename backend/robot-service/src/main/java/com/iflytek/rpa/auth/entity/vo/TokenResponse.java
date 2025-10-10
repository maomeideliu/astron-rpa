package com.iflytek.rpa.auth.entity.vo;

/**
 * Token响应VO - 用于向前端返回accessToken和refreshToken
 * 
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/9/28
 */
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse() {
    }

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}

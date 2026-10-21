package com.safecircle.dto;

public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private String phoneNumber;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, String name, String phoneNumber) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

package com.safecircle.dto;

import jakarta.validation.constraints.NotBlank;

public class SharingRequestDto {

    @NotBlank(message = "Target phone number is required")
    private String targetPhoneNumber;

    private Integer expiresInMinutes;

    public SharingRequestDto() {
    }

    public SharingRequestDto(String targetPhoneNumber) {
        this.targetPhoneNumber = targetPhoneNumber;
    }

    public SharingRequestDto(String targetPhoneNumber, Integer expiresInMinutes) {
        this.targetPhoneNumber = targetPhoneNumber;
        this.expiresInMinutes = expiresInMinutes;
    }

    public String getTargetPhoneNumber() {
        return targetPhoneNumber;
    }

    public void setTargetPhoneNumber(String targetPhoneNumber) {
        this.targetPhoneNumber = targetPhoneNumber;
    }

    public Integer getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(Integer expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }
}

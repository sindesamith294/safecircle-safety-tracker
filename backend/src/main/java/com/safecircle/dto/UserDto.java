package com.safecircle.dto;

import com.safecircle.model.User;

public class UserDto {

    private Long id;
    private String name;
    private String phoneNumber;
    private String accountStatus;

    public UserDto() {
    }

    public UserDto(Long id, String name, String phoneNumber, String accountStatus) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }

    public static UserDto from(User user) {
        if (user == null) return null;
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getPhoneNumber(),
            user.getAccountStatus() != null ? user.getAccountStatus().name() : null
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}

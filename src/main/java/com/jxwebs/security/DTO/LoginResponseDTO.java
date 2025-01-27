package com.jxwebs.security.DTO;

public class LoginResponseDTO {

    private Integer id;
    private String token;
    private String username;
    private String authorities;

    public LoginResponseDTO(Integer id, String token, String username, String authorities) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.authorities = authorities;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }
}
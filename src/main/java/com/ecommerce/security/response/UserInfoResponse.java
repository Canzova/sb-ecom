package com.ecommerce.security.response;

import java.util.List;

public class UserInfoResponse {


    Long id;
    private String jwtToken;

    private String username;

    private List<String> roles;

    public UserInfoResponse(Long id, String jwtToken, String username, List<String> roles) {
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}



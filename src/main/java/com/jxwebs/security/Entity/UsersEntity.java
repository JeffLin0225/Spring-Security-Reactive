package com.jxwebs.security.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "Users")
public class UsersEntity {

    @Id
    private Integer id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("authorities")
    private String authorities;


    private String gender;

    private String birth;

    private String email;

    private String phone;

    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  created_at;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated_at;

//    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
//    private List<Reply> reply = new ArrayList<>();
//    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
//    private List<Customerservice> customerservice = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
package io.github.eschoe.reactivemockapi.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("API_USER")
public class ApiUser {

    @Id
    private String userid;
    private String username;
    private String password;

    public ApiUser(String userid, String username, String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
    }

    public ApiUser(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }

    public String getUserid() {
        return this.userid;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

}

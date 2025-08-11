package io.github.eschoe.reactivemockapi.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("API_USER")
public class ApiUser {

    @Id
    @Column("userid")
    private String userid;
    @Column("username")
    private String username;
    @Column("password")
    private String password;

    public ApiUser() { }

    @PersistenceCreator
    public ApiUser(String userid, String username, String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) { this.userid = userid; }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) { this.username = username; }

}

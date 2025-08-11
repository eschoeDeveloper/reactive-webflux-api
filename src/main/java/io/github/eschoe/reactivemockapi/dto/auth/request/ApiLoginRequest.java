package io.github.eschoe.reactivemockapi.dto.auth.request;

public class ApiLoginRequest {

    private String userid;
    private String password;

    public ApiLoginRequest(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }

    public String getUserid() { return this.userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

}

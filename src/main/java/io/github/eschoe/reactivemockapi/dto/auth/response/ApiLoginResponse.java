package io.github.eschoe.reactivemockapi.dto.auth.response;

public class ApiLoginResponse {

    private String message;

    public ApiLoginResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return this.message; }
    public void setMessage(String message) { this.message = message; }

}

package info.touret.bookstore.spring;

import java.io.Serializable;

public class APIError implements Serializable {
    private int code;
    private String reason;


    public APIError() {
    }

    public APIError(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

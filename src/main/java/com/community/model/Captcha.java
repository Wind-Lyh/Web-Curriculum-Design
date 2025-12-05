package com.community.model;

public class Captcha {
    private String code;
    private String see;

    public Captcha() {
    }

    public Captcha(String code, String see) {
        this.code = code;
        this.see = see;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSee() {
        return see;
    }

    public void setSee(String see) {
        this.see = see;
    }
}
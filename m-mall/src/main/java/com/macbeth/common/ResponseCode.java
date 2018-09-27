package com.macbeth.common;

public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),ERROR(1,"ERROR"),NEED_LOGIN(10,"NEED_LOGIN"),ILLEGAL_ARGUMENT(2,"ILLIGAL_ARGUMENT");
    private final int code;
    private final String descript;
    ResponseCode(int code,String descript){
        this.code = code;
        this.descript = descript;
    }

    public int getCode() {
        return code;
    }

    public String getDescript() {
        return descript;
    }
}

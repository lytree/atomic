package top.yang.net.enums;

import okhttp3.MediaType;

/**
 * HTTP 客户端接口
 */
public enum HTTPType {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    OPTIONS("OPTIONS");
    private final String httpType;

    public String getHttpType() {
        return httpType;
    }


    HTTPType(String httpType) {
        this.httpType = httpType;
    }
}


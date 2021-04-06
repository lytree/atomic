package top.yang.net.enums;

import okhttp3.*;
import okhttp3.WebSocket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * HTTP 客户端接口
 */
public interface HTTPType {

    String GET = "GET";
    String HEAD = "HEAD";
    String POST = "POST";
    String PUT = "PUT";
    String PATCH = "PATCH";
    String DELETE = "DELETE";



}


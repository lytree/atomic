package top.yang.net;

public enum Level {
    NONE,       //不打印log
    BASIC,      //只打印 请求首行 和 响应首行
    HEADERS,    //打印请求和响应的所有 Header
    BODY        //所有数据全部打印
}
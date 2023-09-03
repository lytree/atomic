package top.lytree.model.exception.result;

/**
 * @author Y
 */

public enum ServerCode implements ResultCode {
    //-------------------成功-------------------//
    SUCCESS("S0000", "操作成功！"),


    //--------------------系统相关9000 - 9999----------------------//
    INVALID_PARAM("S9000", "非法参数！"),


    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION("S9001", "请求方法不支持！"),

    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION("S9002", "请求数据类型不支持！"),

    EMAIL_IS_NOT_CONNECT("S9003", "无法连接到邮箱服务器，请检查邮箱配置"),

    EMAIL_SEND_IS_FAIL("S9004", "邮件发送失败，请检查 SMTP 服务配置是否正确"),
    NO_ACCESS("S9005", "无权限访问"),
    DATA_IS_ABNORMAL("S9006", "获取数据异常"),
    MYSQLI_CONNECTION_IS_FAIL("S9007", "MySQL 链接异常"),
    POJO_INSTANCE_FAIL("S9020", "底层pojo类初始化时发生异常"),

    REQUEST_METHOD_NOT_SUPPORTED("S9030", "该接口不支持该请求方法"),

    SERVER_ERROR("S9998", "抱歉，系统繁忙，请稍后重试！"),

    FAIL("S9999", "系统异常！"),
    ;


    /**
     * 操作代码
     */

    final String code;
    /**
     * 提示信息
     */
    final String message;

    ServerCode(String code, String message) {

        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ServerCode{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}


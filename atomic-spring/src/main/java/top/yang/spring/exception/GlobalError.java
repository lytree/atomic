package top.yang.spring.exception;

public enum GlobalError {



    /**--------------底层数据操作异常（9000~9999）---------------**/
    SYSERR("9999", "系统错误"),
    SUCCESS("0000", "交易成功"),

    ILLEGAL_ARGMENT("9001", "参数错误"),//参数校验错误{参数名}
    ILLEGAL_PARAMETER_TYPE("9002", "参数类型错误"),//参数类型错误(参数名)
    REQUEST_OFTEN("9003", "请求过于频繁"),
    RESPONSE_FAIL("9004", "生成响应数据失败"),
    CORRELATIONID_MISS("9005", "correlationID缺失"),
    SYS_DBERR("9006", "数据库操作错误"),
    QUERY_ERROR("9007", "数据查询异常"),
    LOCK("9008", "已加锁"),
    DATA_ERROR("9009", "数据错误"),
    QUERY_NOEXIST("9010", "查询记录不存在"),
    INSERT_FAIL("9011", "新增数据失败"),
    DELETE_FAIL("9012", "删除数据失败"),
    UPDATE_FAIL("9013", "修改数据失败"),
    INFO_EXIST("9014", "信息已存在"),
    SYS_REDIS_DBERR("9015", "REDIS数据库操作错误"),
    QUERY_REDIS_ERROR("9016", "REIDS数据查询异常"),
    ID_NOT_FOUND("9017", "数据未找到@Id注解"),
    ID_CLASS_NOT_FOUND("9018", "数据未找到@IdClass注解"),
    NEW_INSTANCE_FAIL("9019", "实例化类型失败"),
    MUTI_KEY_ID_NOT_FOUND("9020", "保存多主键实例时部分主键为空"),
    ID_GENERATE_FAIL("9021", "主键编号生成失败"),
    POJO_INSTANCE_FAIL("9022", "底层pojo类初始化时发生异常"),
    MQ_CONSUMER_NOT_FOUND("9023", "MQConsumer配置未找到" ),
    MQ_HANDLER_NOT_FOUND("9024", "未找到对应Consumer处理器"),
    MQ_CONSUMER_NOT_MATCH("9025", "未匹配到Consumer"),
    RECORD_NOT_EXIST("9026", "更新时未找到记录"),
    PRIMARY_KEY_IS_EMPTY("9027", "传入主键为空"),
    UNKNOW_CLASS("9028", "数据未找到@IdClass注解"),

;

GlobalError(String code, String msg) {
    this.code = code;
    this.msg = msg;
}

public String code;

public String msg;



}

package top.yang.exceptions;

public class UnknownComplexConditionClassException extends RuntimeException {

    @Override
    public String getMessage() {
        return "未知的复杂条件字段类型";
    }
}

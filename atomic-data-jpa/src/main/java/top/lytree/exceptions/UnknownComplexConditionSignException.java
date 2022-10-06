package top.lytree.exceptions;

public class UnknownComplexConditionSignException extends RuntimeException {

    @Override
    public String getMessage() {
        return "未知的复杂条件查询符号";
    }
}

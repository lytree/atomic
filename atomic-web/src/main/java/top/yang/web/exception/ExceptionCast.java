package top.yang.web.exception;

import top.yang.web.enums.ResultCode;

public class ExceptionCast {
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}

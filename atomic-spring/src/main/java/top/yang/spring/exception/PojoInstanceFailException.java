package top.yang.spring.exception;

public class PojoInstanceFailException extends SystemException {

    public PojoInstanceFailException() {
        this.code = GlobalError.POJO_INSTANCE_FAIL.code;
        this.msg = GlobalError.POJO_INSTANCE_FAIL.msg;
    }
}

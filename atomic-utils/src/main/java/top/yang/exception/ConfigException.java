package top.yang.exception;

public class ConfigException extends AtomicException {
    public ConfigException() {
    }

    public ConfigException(String code, String msg) {
        super(code, msg);
    }

    @Override
    public String toString() {
        return "获取配置文件信息异常  ConfigException{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}

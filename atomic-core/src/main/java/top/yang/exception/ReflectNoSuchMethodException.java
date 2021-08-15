package top.yang.exception;

public class ReflectNoSuchMethodException extends RuntimeException {

    private Class clazz;

    private String method;

    private Class[] paramClazz;

    public ReflectNoSuchMethodException(Class clazz, String method, Class[] paramClazz) {
        this.clazz = clazz;
        this.method = method;
        this.paramClazz = paramClazz;
    }

    @Override
    public String getMessage() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("对象【"+clazz.getName()+"】找不到方法【"+method+"】所属参数为：");
        for (int i = 0; i < paramClazz.length; i++) {

            buffer.append("参数" + i +"【"+paramClazz[i].getName()+"】");

        }
        return buffer.toString();
    }
}

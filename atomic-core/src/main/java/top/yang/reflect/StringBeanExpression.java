package top.yang.reflect;

/**
 * @className: StringBeanExpression
 * @description: 用于将String形式的类关系描述，切割成各个部分
*/
public class StringBeanExpression {

    private String source;

    public StringBeanExpression(String source) {
        this.source = source;
    }

    public static StringBeanExpression from(String str) {
        return new StringBeanExpression(str);
    }

    /**
      * @methodName: hasSeparator
      * @description: 判断是否存在分隔符
    **/
    public boolean hasSeparator(String separator) {
        if (source.indexOf(separator) > -1) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
      * @methodName: cutFirstBy
      * @description: 根据分隔符将字符串裁剪成两部分，取第一部分
    **/
    public StringBeanExpression cutFirstBy(String separator) {

        if (hasSeparator(separator)) {
            return new StringBeanExpression(source.substring(0, source.indexOf(separator)));
        } else {
            return null;
        }
    }


    /**
      * @methodName: cutLastBy
      * @description: 根据分隔符将字符串裁剪成两部分，取第二部分
    **/
    public StringBeanExpression cutLastBy(String separator) {
        if (hasSeparator(separator)) {
            return new StringBeanExpression(source.substring(source.indexOf(separator) + 1));
        } else {
            return null;
        }
    }
    
    /**
      * @methodName: field
      * @description: 从字符串中获取字段名，如果有其他字符则去除
    **/
    public String field() {

        if (source.indexOf("[") > 1) {
            return source.substring(0, source.indexOf("["));
        } else {
            return source;
        }
    }

    /**
      * @methodName: index
      * @description: 获取表达式中的索引
    **/
    public Integer index() {
        if (source.indexOf("[") > 1) {
            return Integer.valueOf(source.substring(source.indexOf("[") + 1, source.indexOf("]")));
        } else {
            return null;
        }
    }

    
    /**
      * @methodName: get
      * @description: 获取字符串
    **/
    public String get() {
        return source;
    }

}

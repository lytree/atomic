package top.lytree.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import top.lytree.collections.ArraysUtils;
import top.lytree.lang.StringUtils;

/**
 * @author pride
 */
public class FieldUtils extends org.apache.commons.lang3.reflect.FieldUtils {

    /**
     * 修饰符枚举
     *
     * @author looly
     */
    public enum ModifierType {
        /**
         * public修饰符，所有类都能访问
         */
        PUBLIC(Modifier.PUBLIC),
        /**
         * private修饰符，只能被自己访问和修改
         */
        PRIVATE(Modifier.PRIVATE),
        /**
         * protected修饰符，自身、子类及同一个包中类可以访问
         */
        PROTECTED(Modifier.PROTECTED),
        /**
         * static修饰符，（静态修饰符）指定变量被所有对象共享，即所有实例都可以使用该变量。变量属于这个类
         */
        STATIC(Modifier.STATIC),
        /**
         * final修饰符，最终修饰符，指定此变量的值不能变，使用在方法上表示不能被重载
         */
        FINAL(Modifier.FINAL),
        /**
         * synchronized，同步修饰符，在多个线程中，该修饰符用于在运行前，对他所属的方法加锁，以防止其他线程的访问，运行结束后解锁。
         */
        SYNCHRONIZED(Modifier.SYNCHRONIZED),
        /**
         * （易失修饰符）指定该变量可以同时被几个线程控制和修改
         */
        VOLATILE(Modifier.VOLATILE),
        /**
         * （过度修饰符）指定该变量是系统保留，暂无特别作用的临时性变量，序列化时忽略
         */
        TRANSIENT(Modifier.TRANSIENT),
        /**
         * native，本地修饰符。指定此方法的方法体是用其他语言在程序外部编写的。
         */
        NATIVE(Modifier.NATIVE),

        /**
         * abstract，将一个类声明为抽象类，没有实现的方法，需要子类提供方法实现。
         */
        ABSTRACT(Modifier.ABSTRACT),
        /**
         * strictfp，一旦使用了关键字strictfp来声明某个类、接口或者方法时，那么在这个关键字所声明的范围内所有浮点运算都是精确的，符合IEEE-754规范的。
         */
        STRICT(Modifier.STRICT);

        /**
         * 修饰符枚举对应的int修饰符值
         */
        private final int value;

        /**
         * 构造
         *
         * @param modifier 修饰符int表示，见{@link Modifier}
         */
        ModifierType(int modifier) {
            this.value = modifier;
        }

        /**
         * 获取修饰符枚举对应的int修饰符值，值见{@link Modifier}
         *
         * @return 修饰符枚举对应的int修饰符值
         */
        public int getValue() {
            return this.value;
        }
    }

    /**
     * 是否同时存在一个或多个修饰符（可能有多个修饰符，如果有指定的修饰符则返回true）
     *
     * @param clazz         类
     * @param modifierTypes 修饰符枚举
     *
     * @return 是否有指定修饰符，如果有返回true，否则false，如果提供参数为null返回false
     */
    public static boolean hasModifier(Class<?> clazz, ModifierType... modifierTypes) {
        if (null == clazz || ArraysUtils.isEmpty(modifierTypes)) {
            return false;
        }
        return 0 != (clazz.getModifiers() & modifiersToInt(modifierTypes));
    }

    /**
     * 是否同时存在一个或多个修饰符（可能有多个修饰符，如果有指定的修饰符则返回true）
     *
     * @param constructor   构造方法
     * @param modifierTypes 修饰符枚举
     *
     * @return 是否有指定修饰符，如果有返回true，否则false，如果提供参数为null返回false
     */
    public static boolean hasModifier(Constructor<?> constructor, ModifierType... modifierTypes) {
        if (null == constructor || ArraysUtils.isEmpty(modifierTypes)) {
            return false;
        }
        return 0 != (constructor.getModifiers() & modifiersToInt(modifierTypes));
    }

    /**
     * 是否同时存在一个或多个修饰符（可能有多个修饰符，如果有指定的修饰符则返回true）
     *
     * @param method        方法
     * @param modifierTypes 修饰符枚举
     *
     * @return 是否有指定修饰符，如果有返回true，否则false，如果提供参数为null返回false
     */
    public static boolean hasModifier(Method method, ModifierType... modifierTypes) {
        if (null == method || ArraysUtils.isEmpty(modifierTypes)) {
            return false;
        }
        return 0 != (method.getModifiers() & modifiersToInt(modifierTypes));
    }

    /**
     * 是否同时存在一个或多个修饰符（可能有多个修饰符，如果有指定的修饰符则返回true）
     *
     * @param field         字段
     * @param modifierTypes 修饰符枚举
     *
     * @return 是否有指定修饰符，如果有返回true，否则false，如果提供参数为null返回false
     */
    public static boolean hasModifier(Field field, ModifierType... modifierTypes) {
        if (null == field || ArraysUtils.isEmpty(modifierTypes)) {
            return false;
        }
        return 0 != (field.getModifiers() & modifiersToInt(modifierTypes));
    }

    /**
     * 是否是Public字段
     *
     * @param field 字段
     *
     * @return 是否是Public
     */
    public static boolean isPublic(Field field) {
        return hasModifier(field, ModifierType.PUBLIC);
    }

    /**
     * 是否是Public方法
     *
     * @param method 方法
     *
     * @return 是否是Public
     */
    public static boolean isPublic(Method method) {
        return hasModifier(method, ModifierType.PUBLIC);
    }

    /**
     * 是否是Public类
     *
     * @param clazz 类
     *
     * @return 是否是Public
     */
    public static boolean isPublic(Class<?> clazz) {
        return hasModifier(clazz, ModifierType.PUBLIC);
    }

    /**
     * 是否是Public构造
     *
     * @param constructor 构造
     *
     * @return 是否是Public
     */
    public static boolean isPublic(Constructor<?> constructor) {
        return hasModifier(constructor, ModifierType.PUBLIC);
    }

    /**
     * 是否是static字段
     *
     * @param field 字段
     *
     * @return 是否是static
     */
    public static boolean isStatic(Field field) {
        return hasModifier(field, ModifierType.STATIC);
    }

    /**
     * 是否是static方法
     *
     * @param method 方法
     *
     * @return 是否是static
     */
    public static boolean isStatic(Method method) {
        return hasModifier(method, ModifierType.STATIC);
    }

    /**
     * 是否是static类
     *
     * @param clazz 类
     *
     * @return 是否是static
     */
    public static boolean isStatic(Class<?> clazz) {
        return hasModifier(clazz, ModifierType.STATIC);
    }

    /**
     * 是否是合成字段（由java编译器生成的）
     *
     * @param field 字段
     *
     * @return 是否是合成字段
     */
    public static boolean isSynthetic(Field field) {
        return field.isSynthetic();
    }

    /**
     * 是否是合成方法（由java编译器生成的）
     *
     * @param method 方法
     *
     * @return 是否是合成方法
     */
    public static boolean isSynthetic(Method method) {
        return method.isSynthetic();
    }

    /**
     * 是否是合成类（由java编译器生成的）
     *
     * @param clazz 类
     *
     * @return 是否是合成
     */
    public static boolean isSynthetic(Class<?> clazz) {
        return clazz.isSynthetic();
    }

    /**
     * 是否抽象方法
     *
     * @param method 方法
     *
     * @return 是否抽象方法
     */
    public static boolean isAbstract(Method method) {
        return hasModifier(method, ModifierType.ABSTRACT);
    }

    /**
     * 获取字段值
     *
     * @param obj       对象，如果static字段，此处为类
     * @param fieldName 字段名
     *
     * @return 字段值
     *
     * @throws IllegalAccessException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(final Object obj, final String fieldName) throws IllegalAccessException {
        if (null == obj || StringUtils.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName));
    }

    /**
     * 获取静态字段值
     *
     * @param field 字段
     *
     * @return 字段值
     *
     * @throws IllegalAccessException 包装IllegalAccessException异常
     * @since 5.1.0
     */
    public static Object getStaticFieldValue(final Field field) throws IllegalAccessException {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象，static字段则此字段为null
     * @param field 字段
     *
     * @return 字段值
     *
     * @throws IllegalAccessException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, final Field field) throws IllegalAccessException {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            // 静态字段获取时对象为null
            obj = null;
        }
        return field.get(obj);
    }

    /**
     * 是否为父类引用字段<br>
     * 当字段所在类是对象子类时（对象中定义的非static的class），会自动生成一个以"this$0"为名称的字段，指向父类对象
     *
     * @param field 字段
     *
     * @return 是否为父类引用字段
     *
     * @since 5.7.20
     */
    public static boolean isOuterClassField(final Field field) {
        return "this$0".equals(field.getName());
    }
    //-------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 多个修饰符做“与”操作，表示同时存在多个修饰符
     *
     * @param modifierTypes 修饰符列表，元素不能为空
     *
     * @return “与”之后的修饰符
     */
    private static int modifiersToInt(ModifierType... modifierTypes) {
        int modifier = modifierTypes[0].getValue();
        for (int i = 1; i < modifierTypes.length; i++) {
            modifier |= modifierTypes[i].getValue();
        }
        return modifier;
    }
    //-------------------------------------------------------------------------------------------------------- Private method end
}

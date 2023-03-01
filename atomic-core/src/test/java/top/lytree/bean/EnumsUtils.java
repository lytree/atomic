package top.lytree.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.junit.Test;

public class EnumsUtils {

    @Test
    public void getFieldTest(){
        Field field = Enums.getField(Sport.ONE);
        Type genericType = field.getGenericType();
        System.out.println(field);
    }
}

package com.zhl.reflex;

import com.zhl.serialize.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-19 22:08
 */
public class ReflexTest {
    @Test
    void testReflexType() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        /*ClassLoader loader = KryoSerializer.class.getClassLoader();
        Class<?> aClass = loader.loadClass(Person.class.getCanonicalName());*/

        Class<?> aClass = Class.forName(Person.class.getCanonicalName());

        Constructor<?> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();
        System.out.println("o = " + o);
    }

    @Test
    void testReflexField() throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();
        Constructor<?> constructor = loader.loadClass(Person.class.getCanonicalName()).getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();

        //
        Field[] fields = o.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println("field = " + field.getType());
            // 把目标对象中的对应属性值修改为其他值
            field.set(o, "22");
        }

        System.out.println("o = " + o);
    }

    @Test
    void testCanonicalName(){
//        Object person = new Person();
        int[] arr = new int[3];
        String s = new String("a");
//        String name = person.getClass().getName();
//        String name = arr.getClass().getName();
        String name = s.getClass().getName();
        System.out.println("name = " + name);
//        String canonicalName = Person.class.getCanonicalName();
//        String canonicalName = arr.getClass().getCanonicalName();
        String canonicalName = s.getClass().getCanonicalName();
        System.out.println("canonicalName = " + canonicalName);
    }

}

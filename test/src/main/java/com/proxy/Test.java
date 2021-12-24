package com.proxy;

import java.lang.reflect.Proxy;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Test test = new Test();
        A a = new A();
        I a1 = (I) test.run(new A());
        I a2 = (I) test.run(new B());
        I a3 = (I) test.run((I) (a4, b) -> 100);
        System.out.println(a.getClass() + "   " + a1.getClass());
        System.out.println(a + "   " + a1);
        System.out.println(a.getClass() == a1.getClass());
        System.out.println(a1.getClass().getSuperclass());

        System.out.println(a1);
        System.out.println(a2);
        System.out.println(a1.add(1, 2));
        System.out.println(a2.add(1, 2));
        Class clazz = Class.forName("com.proxy.B");
        System.out.println(clazz);
        System.out.println(clazz.newInstance());
    }

    public Object run(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), (proxy, method, args) -> {
            Object returnValue = method.invoke(target, args);
            return returnValue;
        });
    }
}

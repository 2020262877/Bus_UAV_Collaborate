package edu.nwpu.test;

import org.junit.Test;

public class TestStaticOrder {

    static {
        System.out.println("静态代码块1执行");
    }

    private static int a = 4;
    private static final int b = ++a;

    static {
        System.out.println("静态代码块2执行");
        System.out.println("b=" + b);
    }

    @Test
    public void main() {
        TestStaticOrder testStaticOrder = new TestStaticOrder();
    }

}

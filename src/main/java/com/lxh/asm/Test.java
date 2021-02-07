package com.lxh.asm;

import com.lxh.asm.generator.ExampleGenerator;
import com.lxh.util.MyClassLoader;
import lombok.SneakyThrows;

public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        //直接将二进制流加载到内存中
        MyClassLoader loader1 = new MyClassLoader("loader1");
        loader1.setPath("/Users/lxh/Downloads/workspace/lxh/agent/target/classes/com/lxh/asm/");

        Class<?> aClass = loadClassByMyClassLoader("com.lxh.asm.Account", loader1);
        Object instance = aClass.newInstance();
        //通过反射调用main方法
        aClass.getMethods()[0].invoke(instance,null);
    }

    /**
     * 在这里可以配置好参数 但是有一个主节点来作为调度系统 动态的将类发送到一个主机，可以由操作系统完成 比如调度时间、配置文件 是不是这样就可以作 map reduce
     * 操作了
     * @param name
     * @param loader
     * @throws Exception
     */
    private static Class<?> loadClassByMyClassLoader(String name, ClassLoader loader) throws Exception {
        Class<?> c = loader.loadClass(name);
        return c;
    }

}

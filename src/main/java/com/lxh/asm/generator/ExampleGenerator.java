package com.lxh.asm.generator;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.SneakyThrows;

import java.io.FileOutputStream;

public class ExampleGenerator extends ClassLoader implements Opcodes {

    @SneakyThrows
    public static void main(String[] args) {
        //定义一个叫做Example的类
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_1,ACC_PUBLIC, "Example", null,"java/lang/Object",null);

        // 生成默认的构造方法
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        //生成构造方法的字节码指令
        mw.visitVarInsn(ALOAD, 0);
        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(1, 1);
        mw.visitEnd();

        //生成main方法
        mw = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null);

        //生成main方法中的字节码指令
        mw.visitFieldInsn(GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");

        mw.visitLdcInsn("Hello world!");
        mw.visitMethodInsn(INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(2, 2);

        //字节码生成完成
        mw.visitEnd();
        // 获取生成的class文件对应的二进制流
        byte[] code = cw.toByteArray();


        //将二进制流写到本地磁盘上
        FileOutputStream fos = new FileOutputStream("target/classes/com/lxh/asm/generator/Example.class");
        fos.write(code);
        fos.close();

        //直接将二进制流加载到内存中
        ExampleGenerator loader = new ExampleGenerator();
        Class<?> exampleClass = loader.defineClass("Example", code, 0, code.length);

        //通过反射调用main方法
        exampleClass.getMethods()[0].invoke(null, new Object[] { null });

    }
}

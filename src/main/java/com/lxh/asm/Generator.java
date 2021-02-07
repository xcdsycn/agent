package com.lxh.asm;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;

public class Generator {
    @SneakyThrows
    public static void main(String[] args) {
        ClassReader cr = new ClassReader("com.lxh.asm.Account");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
        cr.accept(classAdapter,ClassReader.SKIP_DEBUG );
        byte[] data = cw.toByteArray();
        File file = new File("target/classes/com/lxh/asm/Account.class");
        FileOutputStream fOut = new FileOutputStream(file);
        fOut.write(data);
        fOut.close();
    }
}

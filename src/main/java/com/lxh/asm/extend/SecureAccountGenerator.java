package com.lxh.asm.extend;

import com.lxh.asm.Account;
import com.lxh.asm.AddSecurityCheckClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import lombok.SneakyThrows;

public class SecureAccountGenerator {
    private static AccountGeneratorClassLoader classLoader = new AccountGeneratorClassLoader();

    private static Class secureAccountClass;

    @SneakyThrows
    public Account generateSecureAccount() throws ClassFormatError {
        if (null == secureAccountClass) {
            ClassReader cr = new ClassReader("com.lxh.asm.Account");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
            cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
            byte[] data = cw.toByteArray();
            secureAccountClass = classLoader.defineClassFromClassFile("Account$EnhancedByASM", data);
        }
        return (Account) secureAccountClass.newInstance();
    }

    public static void main(String[] args) {
        SecureAccountGenerator generator = new SecureAccountGenerator();
        generator.generateSecureAccount();
    }

    private static class AccountGeneratorClassLoader extends ClassLoader {
        public Class defineClassFromClassFile(String className, byte[] classFile) throws ClassFormatError {
            return defineClass(className, classFile, 0,
                    classFile.length);
        }
    }
}

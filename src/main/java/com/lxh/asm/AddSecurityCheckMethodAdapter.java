package com.lxh.asm;

import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

public class AddSecurityCheckMethodAdapter extends MethodAdapter {
    public AddSecurityCheckMethodAdapter(MethodVisitor mv) {
        super(mv);
    }

    public void visitCode() {
        visitMethodInsn(Opcodes.INVOKESTATIC, "SecurityChecker",
                "checkSecurity", "()V");
    }
}

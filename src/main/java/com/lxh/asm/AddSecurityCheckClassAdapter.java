package com.lxh.asm;



import com.lxh.asm.extend.ChangeToChildConstructorMethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;

public class AddSecurityCheckClassAdapter  extends ClassAdapter {
    public AddSecurityCheckClassAdapter(ClassVisitor classVisitor) {
        //Responsechain 的下一个 ClassVisitor，这里我们将传入 ClassWriter，
        // 负责改写后代码的输出
        super(classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name,
        final String desc, final String signature, final String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature,exceptions);
            MethodVisitor wrappedMv = mv;
            if (mv != null) {
                // 对于 "operation" 方法
                if (name.equals("operation")) {
                    // 使用自定义 MethodVisitor，实际改写方法内容
                    wrappedMv = new AddSecurityCheckMethodAdapter(mv);
                } else if(name.equalsIgnoreCase("<init>")) {
                    wrappedMv = new ChangeToChildConstructorMethodAdapter(mv, "com.lxh.asm.Account$EnhancedByASM");
                }
            }
            return wrappedMv;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String enhancedName = name + "$EnhancedByASM";
        String enhancedSuperName = name;
        super.visit(version,access,enhancedName, signature, enhancedSuperName, interfaces);
    }
}

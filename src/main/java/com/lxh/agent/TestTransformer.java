package com.lxh.agent;

import javassist.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * 类转换器的作用主要是在某个类的字节码被 JVM 读入之后，在 Java 堆上创建 Class 对象之前，
 * JVM 会遍历所有的 instrumentation 实例，并执行其中的所有的 ClassFileTransformer 的 transform 方法，
 * 其中关于启动时加载的 javaAgent 重点需要关注的入参：
 */
@Slf4j
@Data
public class TestTransformer implements ClassFileTransformer {

    // 被处理的方法列表
    private Map<String, Set<String>> methodMap;
    final static String prefix = "\nlong startTime = System.currentTimeMillis();System.out.println(\"Agent source ==> 中间可以做其他处理\");\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

    /**
     * @param loader
     * @param className           当前类的限定类名
     * @param classBeingRedefined
     * @param protectionDomain
     * @param classfileBuffer     当前类的以 byte 数组呈现的字节码数据（可能跟 class 文件的数据不一致,因为此处的 byte 数据是此类最新的字节码数据，
     *                            即此数据可能是原始字节码数据被其他增强方法增强之后的自己买数据）
     * @return
     * @throws IllegalClassFormatException
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        String packageClass = className.replaceAll("/", ".");
        if (methodMap.containsKey(packageClass)) {
            CtClass ctClass = null;
            Set<String> methodSet = methodMap.get(packageClass);
            try {
                ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
                if (!ctClass.isInterface()) {
                    CtBehavior[] declaredBehaviors = ctClass.getDeclaredBehaviors();
                    for (CtBehavior behavior : declaredBehaviors) {
                        if (methodSet.contains(behavior.getName())) {
                            String methodName = behavior.getName();
                            String outputStr = "\nSystem.out.println(\"Agent source ==> this method " + methodName
                                    + " cost:\" +(endTime - startTime) +\"ms.\");";

                            System.out.println("Agent ==> Inject byte code class: " + packageClass + " method: " + behavior.getName());
                            // 方法一：关键在这里，可以动态的执行Java代码，而且这段代码是段文本，在一段代码的前后
                            behavior.addLocalVariable("start", CtClass.longType);
                            behavior.insertBefore("start = System.currentTimeMillis();");
                            behavior.insertAfter("System.out.println(\"Agent source ==> Method cost by agent...method: " +
                                    behavior.getName() + " cost: \" + (System.currentTimeMillis() - start ));");

                            CtMethod ctmethod = ctClass.getDeclaredMethod(methodName);// 得到这方法实例
                            String newMethodName = methodName + "$old";// 新定义一个方法叫做比如 longOperation$old
                            ctmethod.setName(newMethodName);// 将原来的方法名字修改

                            // 方法二： 创建新的方法，复制原来的方法，名字为原来的名字
                            CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctClass, null);

                            // 构建新的方法体
                            StringBuilder bodyStr = new StringBuilder();
                            bodyStr.append("{");
                            bodyStr.append(prefix);
                            bodyStr.append(newMethodName + "($$);\n");// 调用原有代码，类似于method();($$)表示所有的参数
                            bodyStr.append(postfix);
                            bodyStr.append(outputStr);
                            bodyStr.append("}");

                            newMethod.setBody(bodyStr.toString());// 替换新方法
                            ctClass.addMethod(newMethod);// 增加新方法
                            log.error(outputStr);
                        }
                    }
                }
                return ctClass.toBytecode();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (ctClass != null) {
                    ctClass.detach();
                }
            }
        }
        //进行对应类字节码的操作，并返回新字节码数据的byte数组，如果返回null，则代码不对此字节码作任何操作
        return classfileBuffer;
    }
}

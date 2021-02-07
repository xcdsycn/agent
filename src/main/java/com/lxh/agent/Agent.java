package com.lxh.agent;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("================================Java Agent premain instrument======================");
        System.out.println("Agent  args: " + agentArgs);
        String[] classMethods = agentArgs.split(";");

        final Map<String, Set<String>> classMethodMap = new HashMap<String, Set<String>>();

        for (String classMethodList : classMethods) {
            int indexOfClass = classMethodList.indexOf(":");
            String className = classMethodList.substring(0, indexOfClass);
            String[] methods = classMethodList.substring(indexOfClass + 1).split(":");
            Set<String> methodSet = new HashSet<>();
            for (String methodName : methods) {
                methodSet.add(methodName);
            }
            classMethodMap.put(className, methodSet);
        }
        TestTransformer testTransformer = new TestTransformer();
        testTransformer.setMethodMap(classMethodMap);
        inst.addTransformer(testTransformer);

    }
}

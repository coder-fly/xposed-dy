package com.spark.xposeddy.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    /**
     * 打印所有方法
     */
    public static void printMethods(Class<?> clazz) {
        List<StringBuilder> list = getMethods(clazz);
        int size = list.size();
        if (0 < size) {
            for (int i = 0; i < size; i++) {
                TraceUtil.e(list.get(i).toString());
            }
        } else {
            TraceUtil.e("没有方法！");
        }
    }

    /**
     * 获取所有自身的方法
     *
     * @return 所有自身的方法【每一个方法添加到StringBuilder中，最后保存到一个List集合中】
     */
    public static List<StringBuilder> getMethods(Class<?> clazz) {
        List<StringBuilder> list = new ArrayList<StringBuilder>();
        do {
            Method[] methods = clazz.getDeclaredMethods();
            int len = methods.length;

            StringBuilder sb = null;
            for (int i = 0; i < len; i++) {
                Method method = methods[i];
                sb = new StringBuilder();

                // 修饰符
                String modifier = Modifier.toString(method.getModifiers());
                sb.append(modifier + " ");

                // 返回值类型
                Class<?> returnClass = method.getReturnType();
                String returnType = returnClass.getSimpleName();
                sb.append(returnType + " ");

                // 方法名
                String methodName = method.getName();
                sb.append(methodName + " (");

                // 形参列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                int length = parameterTypes.length;

                for (int j = 0; j < length; j++) {
                    Class<?> parameterType = parameterTypes[j];
                    // 形参类型
                    String parameterTypeName = parameterType.getSimpleName();
                    if (j < length - 1) {
                        sb.append(parameterTypeName + ", ");
                    } else {
                        sb.append(parameterTypeName);
                    }
                }

                sb.append(") {}");
                list.add(sb);
            }
        } while ((clazz = clazz.getSuperclass()) != null);

        return list;
    }
}

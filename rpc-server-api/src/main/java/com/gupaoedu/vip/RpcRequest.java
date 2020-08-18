package com.gupaoedu.vip;

import java.io.Serializable;

/**
 * 请求对象
 * @author : lipu
 * @since : 2020-08-18 21:36
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -2723257266354273507L;

    private String className;
    private String methodName;
    private Object[] parameters;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}

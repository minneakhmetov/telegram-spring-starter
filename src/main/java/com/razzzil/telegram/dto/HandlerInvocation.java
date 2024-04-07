package com.razzzil.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AllArgsConstructor
@Data
public class HandlerInvocation {
    private Method method;
    private Object bean;

    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(bean, args);
    }

    public Parameter[] getMethodParameters() {
         return method.getParameters();
    }
}

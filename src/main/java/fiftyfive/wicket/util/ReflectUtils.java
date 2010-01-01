/*
 * Copyright 2010 55 Minutes (http://www.55minutes.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fiftyfive.wicket.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Helpers for reflection tasks used internally by the 55 Minutes
 * Wicket library. Unlike the standard Java reflection methods,
 * these helpers do not throw checked exceptions.
 *
 * @author Matt Brictson
 */
public class ReflectUtils
{
    /**
     * Returns a method with the given name from the specified class. The
     * method must have zero arguments.
     *
     * @throws IllegalArgumentException if no method could be found
     */
    public static Method getZeroArgMethod(Class cls, String methodName)
    {
        if(null == cls)
        {
            throw new IllegalArgumentException("cls cannot be null");
        }
        if(null == methodName)
        {
            throw new IllegalArgumentException("methodName cannot be null");
        }
        
        // First check for method using getMethod()
        Method method = null;
        try
        {
            method = cls.getMethod(methodName, new Class[0]);
        }
        catch(NoSuchMethodException ignore) {}
        
        // If that fails, use getDeclaredMethod() going up the class hierarchy
        Class sup = cls;
        while(null == method && sup != null)
        {
            try
            {
                method = sup.getDeclaredMethod(methodName, new Class[0]);
            }
            catch(NoSuchMethodException ignore) {}
            sup = sup.getSuperclass();
        }
        
        // Last resort: throw IllegalArgumentException
        if(method == null)
        {
            throw new IllegalArgumentException(
                "Cannot find \"" + methodName + "\" method on : " + cls
            );
        }
        return method;
    }
    
    /**
     * Executes the specified method on the given bean. The method must exist
     * and require zero arguments.
     * @return The return value of the method, if any
     * @throws IllegalArgumentException if the method does not exist
     * @throws RuntimeException if the invocation fails
     */
    public static Object invokeZeroArgMethod(Object bean, String methodName)
    {
        Method method = getZeroArgMethod(bean.getClass(), methodName);
        
        if(!Modifier.isPublic(method.getModifiers()))
        {
            method.setAccessible(true);
        }
        Throwable cause = null;
        try
        {
            return method.invoke(bean);
        }
        catch(InvocationTargetException ite)
        {
            cause = ite.getCause();
        }
        catch(Exception e)
        {
            cause = e;
        }
        if(cause instanceof RuntimeException)
        {
            throw (RuntimeException) cause;
        }
        throw new RuntimeException(cause);
    }
    
    /**
     * This class is not meant to be constructed.
     */
    private ReflectUtils()
    {
    }
}

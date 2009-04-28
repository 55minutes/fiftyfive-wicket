/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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

import org.junit.Assert;
import org.junit.Test;

public class ReflectUtilsTest
{
    @Test
    public void testInvokeZeroArgMethod()
    {
        Assert.assertNull(ReflectUtils.invokeZeroArgMethod(this, "voidMethod"));
        Assert.assertEquals(
            "public",
            ReflectUtils.invokeZeroArgMethod(this, "publicMethod")
        );
        Assert.assertEquals(
            "protected",
            ReflectUtils.invokeZeroArgMethod(this, "protectedMethod")
        );
        Assert.assertEquals(
            "private",
            ReflectUtils.invokeZeroArgMethod(this, "privateMethod")
        );
        Assert.assertEquals(
            "default",
            ReflectUtils.invokeZeroArgMethod(this, "defaultMethod")
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvokeZeroArgMethod_arg()
    {
        ReflectUtils.invokeZeroArgMethod(this, "methodWithArg");
    }
    
    @Test(expected=TestException.class)
    public void testInvokeZeroArgMethod_exception()
    {
        ReflectUtils.invokeZeroArgMethod(this, "exceptionalMethod");
    }

    public void voidMethod()
    {
    }
    
    public String publicMethod()
    {
        return "public";
    }
    
    protected String protectedMethod()
    {
        return "protected";
    }
    
    private String privateMethod()
    {
        return "private";
    }
    
    String defaultMethod()
    {
        return "default";
    }
    
    public String methodWithArg(String arg)
    {
        return "arg";
    }
    
    public void exceptionalMethod()
    {
        throw new TestException();
    }
    
    private static class TestException extends RuntimeException
    {
    }
}

/**
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.test;

import org.apache.wicket.model.IModel;

/**
 * Holds a value that will not be serialized. This is helpful
 * in unit tests where you need to construct a quick and dirty model for a
 * non-serializable object, and therefore {@code Model.of()} will not work.
 * This class is not intended for production code.
 * 
 * @since 2.0.2
 */
public class TransientModel<T> implements IModel<T>
{
    private transient T object;
    
    /**
     * Convience method for constructing a TransientModel instance.
     * These statements are equivalent:
     * <pre class="example">
     * IModel&lt;String&gt; = TransientModel.of(value);
     * IModel&lt;String&gt; = new TransientModel&lt;String&gt;(value);</pre>
     */
    public static <T> TransientModel<T> of(T value)
    {
        return new TransientModel<T>(value);
    }
    
    /**
     * Constructs a model that will hold the given value.
     */
    public TransientModel(T value)
    {
        super();
        setObject(value);
    }
    
    public T getObject()
    {
        return this.object;
    }
    
    public void setObject(T value)
    {
        this.object = value;
    }
    
    public void detach()
    {
        // pass
    }
}

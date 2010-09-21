/**
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

package fiftyfive.wicket.js.locator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fiftyfive.util.Assert;
import org.apache.wicket.ResourceReference;

/**
 * Holds a tree of JavaScript dependencies as they are traversed, ensuring
 * that the ordering is properly maintained and duplicates are ignored. Once
 * traversal is complete, the dependencies can be iterated in the order they
 * should appear in the &lt;head&gt; by calling {@link #iterator iterator()}.
 * <p>
 * A property is also maintained to indicate whether CSS is needed by the
 * JavaScript dependencies. Currently this is only used to indicate the need
 * for jQuery UI's CSS theme, based on whether or not jQuery UI is a script
 * dependency. An arbitrary number of CSS dependencies is not supported.
 */
public class DependencyCollection implements Iterable<ResourceReference>
{
    private boolean _frozen;
    private int _position;
    private ResourceReference _css;
    private List<ResourceReference> _resources;
    
    /**
     * Creates an empty collection.
     */
    public DependencyCollection()
    {
        super();
        _frozen = false;
        _position = 0;
        _css = null;
        _resources = new ArrayList<ResourceReference>();
    }
    
    /**
     * Adds a resource to the collection at the current level of the tree.
     * The level of the tree can be adjusted by first calling
     * {@link #descend descend()} and {@link #ascend ascend()}.
     * 
     * @return {@code true} if the resource did not already exist in the
     *         collection, and was therefore added successfully; {@code false}
     *         if the resource already existed (or was {@code null})
     *         and was ignored.
     */
    public boolean add(ResourceReference ref)
    {
        assertMutable();
        if(null == ref || _resources.contains(ref)) return false;
        _resources.add(_position++, ref);
        return true;
    }
    
    /**
     * Inform the collection that subsequent calls to {@link #add add()}
     * should be treated as descendents of the most recently added resource.
     * Once all descendents have been added, {@link #ascend ascend()} should
     * be called to move the insertion cursor back "up" the tree. Calls to
     * {@code descend()} can be nested, as long as they are balanced with an
     * equal number of calls to {@link #ascend ascend()}.
     */
    public void descend()
    {
        assertMutable();
        _position--;
    }
    
    /**
     * Inform the collection that we are finished adding descendent resources.
     * Subsequent calls to {@link #add add()} will be treated as siblings, as
     * opposed to children.
     * 
     * @see #ascend
     */
    public void ascend()
    {
        assertMutable();
        _position++;
    }
    
    /**
     * Returns a reference to the jQuery UI CSS theme, if it is needed by
     * one of the JavaScript dependencies. Otherwise returns {@code null}.
     */
    public ResourceReference getCss()
    {
        return _css;
    }
    
    /**
     * Sets the CSS resource that should accompany these JavaScript
     * dependencies, if any. The default is {@code null}.
     */
    public void setCss(ResourceReference css)
    {
        assertMutable();
        this._css = css;
    }
    
    /**
     * Iterate over all dependencies in the order they should appear in the
     * &lt;head&gt;.
     */
    public Iterator<ResourceReference> iterator()
    {
        return _resources.iterator();
    }
    
    /**
     * Returns {@code true} if this collection does not contain any script
     * references.
     */
    public boolean isEmpty()
    {
        return _resources.size() == 0;
    }
    
    /**
     * Make this object immutable so that any modifications will cause
     * IllegalStateException to be thrown.
     */
    public void freeze()
    {
        _frozen = true;
    }
    
    /**
     * Copy internal state to another instance. The frozen status will not
     * be copied.
     */
    public void copyTo(DependencyCollection other)
    {
        Assert.notNull(other);
        other._position = this._position;
        other._css = this._css;
        other._resources = new ArrayList<ResourceReference>(this._resources);
    }
    
    private void assertMutable()
    {
        if(_frozen)
        {
            throw new IllegalStateException("Frozen. Cannot be modified.");
        }
    }
}

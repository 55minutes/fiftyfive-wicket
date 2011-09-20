/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Args;

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
 * 
 * @since 2.0
 */
public class DependencyCollection implements Iterable<ResourceReference>
{
    private boolean frozen;
    private int position;
    private ResourceReference css;
    private List<ResourceReference> resources;
    
    /**
     * Creates an empty collection.
     */
    public DependencyCollection()
    {
        super();
        this.frozen = false;
        this.position = 0;
        this.css = null;
        this.resources = new ArrayList<ResourceReference>();
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
        if(null == ref || this.resources.contains(ref)) return false;
        this.resources.add(this.position++, ref);
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
        this.position--;
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
        this.position++;
    }
    
    /**
     * Returns a reference to the jQuery UI CSS theme, if it is needed by
     * one of the JavaScript dependencies. Otherwise returns {@code null}.
     */
    public ResourceReference getCss()
    {
        return this.css;
    }
    
    /**
     * Sets the CSS resource that should accompany these JavaScript
     * dependencies, if any. The default is {@code null}.
     */
    public void setCss(ResourceReference css)
    {
        assertMutable();
        this.css = css;
    }
    
    /**
     * Iterate over all dependencies in the order they should appear in the
     * &lt;head&gt;.
     */
    public Iterator<ResourceReference> iterator()
    {
        return this.resources.iterator();
    }
    
    public ResourceReference getRootReference()
    {
        return isEmpty() ? null : this.resources.get(this.resources.size() - 1);
    }
    
    /**
     * Returns {@code true} if this collection does not contain any script
     * references.
     */
    public boolean isEmpty()
    {
        return this.resources.size() == 0;
    }
    
    /**
     * Make this object immutable so that any modifications will cause
     * IllegalStateException to be thrown.
     */
    public void freeze()
    {
        this.frozen = true;
    }
    
    /**
     * Copy internal state to another instance. The frozen status will not
     * be copied.
     */
    public void copyTo(DependencyCollection other)
    {
        Args.notNull(other, "other");
        other.position = this.position;
        other.css = this.css;
        other.resources = new ArrayList<ResourceReference>(this.resources);
    }
    
    private void assertMutable()
    {
        if(this.frozen)
        {
            throw new IllegalStateException("Frozen. Cannot be modified.");
        }
    }
}

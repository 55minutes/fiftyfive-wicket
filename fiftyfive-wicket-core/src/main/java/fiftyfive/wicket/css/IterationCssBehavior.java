/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;


/**
 * Emits {@code odd}, {@code even}, {@code first} and {@code last} CSS classes (or any
 * combination thereof) for repeating components. Supports {@link ListView}, {@link Loop} and
 * subclasses of {@link RepeatingView}
 * (e.g. {@link org.apache.wicket.markup.repeater.data.DataView DataView}).
 * <p>
 * To use, attach this behavior to the item being populated. Upon constructing the behavior
 * you can specify exactly which classes you would like the behavior to manage. In this example,
 * we ask for {@code first} and {@code last} to be emitted to each item in a list:
 * 
 * <pre class="example">
 * Example.html
 * 
 * &lt;ul&gt;
 *   &lt;li wicket:id=&quot;items&quot;&gt;&lt;/li&gt;
 * &lt;/ul&gt;</pre>
 * 
 * <pre class="example">
 * Example.java
 * 
 * add(new ListView("items", listOfThreeThings) {
 *     &#64;Override
 *     protected void populateItem(ListItem it)
 *     {
 *         it.add(new IterationCssBehavior("first", "last"));
 *     }
 * });</pre>
 * 
 * <pre class="example">
 * Output
 * 
 * &lt;ul&gt;
 *   &lt;li class=&quot;first&quot;&gt;&lt;/li&gt;
 *   &lt;li&gt;&lt;/li&gt;
 *   &lt;li class=&quot;last&quot;&gt;&lt;/li&gt;
 * &lt;/ul&gt;</pre>
 * 
 * @since 2.0.4
 */
public class IterationCssBehavior extends CssClassModifier
{
    /**
     * Valid CSS classes that this behavior can be configured to emit during the rendering
     * of repeating components.
     */
    public enum CssClass
    {
        /** The "odd" css class, emitted on odd iterations. The first item is considered odd. */
        ODD,

        /** The "even" css class, emitted on even iterations. The first item is considered odd. */
        EVEN,

        /** The "first" css class, emitted on the first iteration only. */
        FIRST,

        /** The "last" css class, emitted on the last iteration only. */
        LAST,
        
        /**
         * The "iteration" css class. It is emitted on every iteration, with a suffix added
         * representing the iteration number. The numbering starts at 1. In other words:
         * {@code iteration1}, {@code iteration2}, {@code iteration3}, etc. 
         */
        ITERATION
    }
    
    private final List<CssClass> classes = new ArrayList<CssClass>();
    
    /**
     * Construct a behavior that will output the specified css classes. The behavior must
     * be added to a repeating item like an {@link Item} or {@link ListItem}.
     * This is a convenience constructor that, while not typesafe, is much more concise.
     * <pre class="example">
     * // These are equivalent:
     * new IterationCssBehavior("odd", "even");
     * new IterationCssBehavior(IterationCssBehavior.CssClass.ODD, IterationCssBehavior.CssClass.EVEN);</pre>
     * 
     * @param classes One or more of the 5 classes declared in the {@link CssClass} enum.
     * 
     * @throws IllegalArgumentException if one or more of the specified strings does not
     *                                  exactly match a {@link CssClass} value (case-insensitive)
     */
    public IterationCssBehavior(String... classes)
    {
        for(String cls : classes)
        {
            this.classes.add(CssClass.valueOf(cls.toUpperCase()));
        }
    }

    /**
     * Construct a behavior that will output the specified css classes. The behavior must
     * be added to a repeating item like an {@link Item} or {@link ListItem}.
     * 
     * @param classes One or more of the 5 classes declared in the {@link CssClass} enum.
     */
    public IterationCssBehavior(CssClass... classes)
    {
        this.classes.addAll(Arrays.asList(classes));
    }
    
    /**
     * For each of the {@link CssClass} values provided in the constructor, inspect the 
     * component to which this behavior is bound and determine if the css class is applicable.
     * If so, add that class to the Set of classes that will be emitted in the markup.
     * For example, if the class is "odd", determine if the component is odd-numbered within
     * its repeating view; if so, add "odd" to the classes that will be emitted.
     */
    @Override
    protected void modifyClasses(Component component, Set<String> values)
    {
        final int iteration = getIndex(component) + 1;
        final int size = getSize(component);

        for(CssClass cls : this.classes)
        {
            switch(cls)
            {
                case ODD:
                    if(iteration % 2 == 1)
                    {
                        values.remove("even");
                        values.add("odd");
                    }
                    break;
                case EVEN:
                    if(iteration % 2 == 0)
                    {
                        values.remove("odd");
                        values.add("even");
                    }
                    break;
                case FIRST:
                    if(1 == iteration)
                    {
                        if(size > 1)
                        {
                            values.remove("last");
                        }
                        values.add("first");
                    }
                    break;
                case LAST:
                    if(size == iteration)
                    {
                        if(size > 1)
                        {
                            values.remove("first");
                        }
                        values.add("last");
                    }
                    break;
                case ITERATION:
                    values.add("iteration" + iteration);
                    break;
            }
        }
    }
    
    /**
     * Assume that the component is a {@link ListItem},
     * {@link LoopItem}, or {@link Item}
     * and get its index. Note that the index begins from zero.
     * 
     * @throws UnsupportedOperationException if the component is not one of the three supported
     *                                       types
     */
    protected int getIndex(Component component)
    {
        if(component instanceof ListItem)
        {
            return ((ListItem) component).getIndex();
        }
        if(component instanceof LoopItem)
        {
            return ((LoopItem) component).getIndex();
        }
        if(component instanceof Item)
        {
            return ((Item) component).getIndex();
        }
        throw new UnsupportedOperationException(String.format(
            "Don't know how to find the index of component %s (%s). " +
            "Only list.ListItem, list.LoopItem and repeater.Item are supported. " +
            "Perhaps you attached IterationCssBehavior to the wrong component?",
            component.getPath(),
            component.getClass()));
    }
    
    /**
     * Assume that the component has an immediate parent of {@link ListView}, {@link Loop}, or
     * {@link RepeatingView} and use what we know about those implementations to infer the
     * size of the list that is being iterated. Note that in the case of pagination, this is the
     * size of the visible items (i.e the current page), not the total size that includes other
     * pages.
     * 
     * @throws UnsupportedOperationException if the parent component is not one of the three
     *                                       supported types
     */
    protected int getSize(Component component)
    {
        MarkupContainer parent = component.getParent();
        
        if(parent instanceof ListView)
        {
            return ((ListView) parent).getViewSize();
        }
        if(parent instanceof Loop)
        {
            return ((Loop) parent).getIterations();
        }
        if(parent instanceof RepeatingView)
        {
            // TODO: more efficent way?
            int size = 0;
            Iterator iter = parent.iterator();
            while(iter.hasNext())
            {
                iter.next();
                size ++;
            }
            return size;
        }
        
        throw new IllegalStateException(String.format(
            "Don't know how to find the size of the repeater that contains component " +
            "%s (%s). " +
            "Only list.ListItem, list.LoopItem and repeater.Item are supported. " +
            "Perhaps you attached IterationCssBehavior to the wrong component?",
            component.getPath(),
            component.getClass()));
    }
}

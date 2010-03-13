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
package fiftyfive.wicket.booleanquery;

import java.io.Serializable;
import java.util.*;

/**
 * Class describing the query constructed by the BooleanQueryBuilder.
 *
 * @author Richa Avasthi
 */
public class BooleanQuery implements Serializable, Cloneable
{
    /*
    ** A query consists of two lists: the plus list and the minus list.
    */
    private ArrayList _pluses;
    private ArrayList _minuses;
    
    /**
     * Constructor.
     */
    public BooleanQuery()
    {
        _pluses = new ArrayList();
        _minuses = new ArrayList();
    }
    
    /**
     * Clear the query
     */
    public void clear()
    {
        _pluses.clear();
        _minuses.clear();
    }
    
    /**
     * Return a copy of this object
     */
    public Object clone()
    {
        try
        {
            BooleanQuery newQuery = (BooleanQuery )super.clone();
            newQuery._pluses = (ArrayList )_pluses.clone();
            newQuery._minuses = (ArrayList )_minuses.clone();
            return newQuery;
        }
        catch(CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Return the size of the query, meaning the combined number of plus and
     * minus items contained.
     */
    public int size()
    {
        return _pluses.size() + _minuses.size();
    }
    
    /**
     * Returns the query represented as a string, like so:
     *  "+choiceW, +choiceX, -choiceY, -choiceZ"
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        
        for(Object o : _pluses)
        {
            if(buf.length() == 0)
            {
                buf.append("+" + o.toString());
            }
            else
            {
                buf.append(", +" + o.toString());
            }
        }

        for(Object o : _minuses)
        {
            if(buf.length() == 0)
            {
                buf.append("-" + o.toString());
            }
            else
            {
                buf.append(", -" + o.toString());
            }
        }
        
        return buf.toString();
    }
    
    /**
     * Accessors for the plus and minus lists.
     */
    public List getPluses()
    {
        return _pluses;
    }
    
    public List getMinuses()
    {
        return _minuses;
    }

    /**
     * A convenience method to append an item to the plus list.
     *
     * @see #addObj
     */
    public boolean addPlus(Object obj)
    {
        return addObj(obj, _pluses);
    }
    
    /**
     * A convenience method to remove an item from the plus list.
     *
     * @see #removeObj
     */
    public Object removePlus(Object obj)
    {
        return removeObj(obj, _pluses);
    }

    /**
     * A convenience method to append an item to the minus list.
     *
     * @see #addObj
     */
    public boolean addMinus(Object obj)
    {
        return addObj(obj, _minuses);
    }
    
    /**
     * A convenience method to remove an item from the minus list.
     *
     * @see #removeObj
     */
    public Object removeMinus(Object obj)
    {
        return removeObj(obj, _minuses);
    }

    /**
     * A convenience method to append an item to a list. Used internally.
     *
     * @param obj   the object to append
     * @param list  the list to append to
     *
     * @return      the result of the add operation as returned from 
     *              java.util.List#add
     *
     * @see java.util.List#add
     */
    private boolean addObj(Object obj, List list)
    {
        return list.add(obj);
    }
    
    /**
     * A convenience method to remove an item from a list if it's
     * present. Used internally.
     *
     * @param obj   the object to remove
     * @param list  the list to remove from
     *
     * @return      the result of the remove operation as returned from 
     *              java.util.List#remove
     *
     * @see java.util.List#indexOf
     * @see java.util.List#remove
     */
    private Object removeObj(Object obj, List list)
    {
        /*
        ** Find the item in the list.  If it's present, remove it.
        */
        int index = list.indexOf(obj);
        
        if(index != -1)
        {
            return list.remove(index);
        }
        return null;
    }
}

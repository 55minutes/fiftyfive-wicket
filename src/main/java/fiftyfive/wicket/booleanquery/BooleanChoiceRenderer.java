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

package fiftyfive.wicket.booleanquery;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Choice renderer for BooleanQuery objects that returns an abbreviated display
 * value when the size of the query is greater than 3.
 *
 * @author Richa Avasthi
 */
public class BooleanChoiceRenderer implements IChoiceRenderer
{
    private IAbbrevChoiceRenderer _abbreviator;
    
    /**
     * BooleanChoiceRenderer constructor
     */
    public BooleanChoiceRenderer(IAbbrevChoiceRenderer abbrev) 
    {
        _abbreviator = abbrev;
    }
    
    public Object getDisplayValue(Object obj)
    {
        Object disp = obj;
        if(obj instanceof BooleanQuery)
        {
            BooleanQuery bq = (BooleanQuery )obj;
            if(bq.size() > 3)
            {
                // we abbreviate
                disp = abbreviate(bq);
            }
            else
            {
                disp = bq.toString();
            }
        }
        return disp;
    }
    
    public String getIdValue(Object obj, int index)
    {
        return Integer.toString(index);
    }
    
    /*
    ** Construct an abbreviated string representation of the boolean query,
    ** based on the value returned by the IAbbrevChoiceRenderer.
    */
    private String abbreviate(BooleanQuery bq)
    {
        StringBuffer buf = new StringBuffer();
        
        for(Object o : bq.getPluses())
        {
            String small = (String )_abbreviator.getAbbrevDisplayValue(o);
            if(buf.length() == 0)
            {
                buf.append("+" + small);
            }
            else
            {
                buf.append(", +" + small);
            }
        }

        for(Object o : bq.getMinuses())
        {
            String small = (String )_abbreviator.getAbbrevDisplayValue(o);
            if(buf.length() == 0)
            {
                buf.append("-" + small);
            }
            else
            {
                buf.append(", -" + small);
            }
        }
        
        return buf.toString();
    }
}

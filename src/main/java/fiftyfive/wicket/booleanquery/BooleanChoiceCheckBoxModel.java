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

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

/**
 * Model to wrap the plus-minus checkboxes for the BooleanQueryBuilder.
 *
 * @author Richa Avasthi
 */
public class BooleanChoiceCheckBoxModel implements IWrapModel
{
    private IModel _choice;
    private boolean _isInclude;
    private BooleanQuery _query;
    
    /**
     * BooleanChoiceCheckBoxModel constructor
     */
    public BooleanChoiceCheckBoxModel(BooleanQuery query, IModel choice, 
                                      boolean isInclude) 
    {
        _query = query;
        _choice = choice;
        _isInclude = isInclude;
    }
    
    public Object getObject()
    {
        Object theChoice = _choice.getObject();
        if(_isInclude)
        {
            return _query.getPluses().contains(theChoice);
        }
        return _query.getMinuses().contains(theChoice);
    }
    
    public void setObject(Object obj)
    {
        Object theChoice = _choice.getObject();
        _query.removePlus(theChoice);
        _query.removeMinus(theChoice);
        
        if(Boolean.TRUE.equals(obj))
        {
            if(_isInclude)
            {
                _query.addPlus(theChoice);
            }
            else
            {
                _query.addMinus(theChoice);
            }
        }
    }
    
    public IModel getWrappedModel()
    {
        return _choice;
    }
    
    public void detach()
    {
    }
}

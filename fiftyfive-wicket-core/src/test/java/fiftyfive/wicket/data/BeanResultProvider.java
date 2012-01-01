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
package fiftyfive.wicket.data;

import java.util.Iterator;

import fiftyfive.wicket.data.DtoDataProvider;

public class BeanResultProvider extends DtoDataProvider<BeanResult,Bean>
{
    private int loadCount = 0;
    
    public int getLoadCount()
    {
        return this.loadCount;
    }
    
    protected BeanResult load(int offset, int amount)
    {
        this.loadCount ++;
        return new BeanResult(offset, amount);
    }
    
    protected Iterator<Bean> iterator(BeanResult result)
    {
        return result.getBeans().iterator();
    }
    
    protected int size(BeanResult result)
    {
        return result.getTotal();
    }
}

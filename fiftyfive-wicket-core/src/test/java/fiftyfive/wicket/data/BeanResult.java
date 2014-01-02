/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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

import java.util.ArrayList;
import java.util.List;

public class BeanResult
{
    private List<Bean> beans;
    private int total;
    
    public BeanResult(int offset, int amount)
    {
        this.beans = new ArrayList<Bean>();
        for(int i=0; i<amount; i++)
        {
            this.beans.add(new Bean(offset + i));
        }
        this.total = 100;
    }
    
    public int getTotal()
    {
        return this.total;
    }
    
    public List<Bean> getBeans()
    {
        return this.beans;
    }
}

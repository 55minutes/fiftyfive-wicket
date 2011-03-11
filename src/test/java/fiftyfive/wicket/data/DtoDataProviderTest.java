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

package fiftyfive.wicket.data;

import fiftyfive.wicket.BaseWicketTest;
import org.junit.Assert;
import org.junit.Test;

public class DtoDataProviderTest extends BaseWicketTest
{
    @Test
    public void testRender() throws Exception
    {
        assertRenderAndClickNext(0);
    }
    
    @Test
    public void testRender_clickNext() throws Exception
    {
        assertRenderAndClickNext(1);
    }
    
    @Test
    public void testRender_clickNextTwice() throws Exception
    {
        assertRenderAndClickNext(2);
    }
    
    private void assertRenderAndClickNext(int clicks) throws Exception
    {
        DtoDataProviderTestPage page = new DtoDataProviderTestPage();
        this.tester.startPage(page);
        
        for(int i=0; i<clicks; i++)
        {
            this.tester.clickLink("paging:next");
        }
        
        this.tester.assertResultPage(
            DtoDataProviderTestPage.class,
            String.format("DtoDataProviderTestPage-%d-expected.html", clicks)
        );
        
        // Data should have been loaded only once per render!
        Assert.assertEquals(1 + clicks, page.getLoadCount());
    }
}

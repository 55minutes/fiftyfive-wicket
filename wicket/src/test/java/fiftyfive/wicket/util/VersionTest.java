/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.util;

import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

public class VersionTest
{
    /**
     * Inspect the wicket jar and verify its version information.
     */
    @Test
    public void testVersionOfJar()
    {
        Date now = new Date();
        Version v = Version.ofJar(org.apache.wicket.Component.class);
        
        Assert.assertEquals("1.4-rc6", v.getVersion());
        Assert.assertEquals("Wicket", v.getTitle());
        Assert.assertTrue(v.getModifiedDate().before(now));
        
        // Wicket 1.4-rc6 was built in June 2009
        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.YEAR, 2009);
        expected.set(Calendar.MONTH, 5);
        expected.set(Calendar.DATE, 1);
        
        Calendar mod = Calendar.getInstance();
        mod.setTime(v.getModifiedDate());
        Assert.assertTrue(mod.after(expected));
    }
}

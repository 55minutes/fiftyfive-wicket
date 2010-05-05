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
package fiftyfive.wicket.resource;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;

import org.apache.wicket.request.target.coding.SharedResourceRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.WebRequestEncoder;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * An extension of SharedResourceRequestTargetUrlCodingStrategy that encodes
 * request parameters as query string parameters, rather than path items.
 * Uses code taken from Wicket's QueryStringUrlCodingStrategy.
 */
public class QueryStringSharedResourceRequestTargetUrlCodingStrategy extends SharedResourceRequestTargetUrlCodingStrategy
{
    public QueryStringSharedResourceRequestTargetUrlCodingStrategy(
        String mountPath, String resourceKey)
    {
        super(mountPath, resourceKey);
    }
    
    // Following code taken from Wicket's QueryStringUrlCodingStrategy.java.
    
    /*
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements.  See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership.
     * The ASF licenses this file to You under the Apache License, Version 2.0
     * (the "License"); you may not use this file except in compliance with
     * the License.  You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    
    @Override
    protected void appendParameters(AppendingStringBuffer url, Map parameters)
    {
        if (parameters != null && parameters.size() > 0)
        {
            final Iterator entries = parameters.entrySet().iterator();
            WebRequestEncoder encoder = new WebRequestEncoder(url);
            while (entries.hasNext())
            {
                Map.Entry entry = (Entry)entries.next();

                if (entry.getValue() != null)
                {
                    encoder.addValue(entry.getKey().toString(), entry.getValue());
                }
            }
        }
    }
}

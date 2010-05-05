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

import java.util.ArrayList;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.target.resource.ISharedResourceRequestTarget;

public final class QueryStringMergedResourceRequestTargetUrlCodingStrategy extends QueryStringSharedResourceRequestTargetUrlCodingStrategy {
	private final ArrayList<String> _mergedKeys;

	public QueryStringMergedResourceRequestTargetUrlCodingStrategy(String mountPath, String resourceKey, ArrayList<String> mergedKeys) {
		super(mountPath, resourceKey);
		_mergedKeys = mergedKeys;
	}

	@Override
	public boolean matches(final IRequestTarget requestTarget) {
		if (requestTarget instanceof ISharedResourceRequestTarget) {
			final ISharedResourceRequestTarget target = (ISharedResourceRequestTarget) requestTarget;
			return super.matches(requestTarget)
					|| _mergedKeys.contains(target.getResourceKey());
		} else {
			return false;
		}
	}
}

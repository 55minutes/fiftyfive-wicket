package fiftyfive.wicket.resource;

import java.util.ArrayList;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.target.resource.ISharedResourceRequestTarget;

// The following code is copied from
// MergedResourceRequestTargetUrlCodingStrategy.java with only minor
// tweaks to extend a different super class.
// Original code Copyright 2010 Stefan Fußenegger. Licensed under
// the Apache Software License, Version 2.0.

/**
 * A re-implementation of the wicketstuff-merged-resources
 * MergedResourceRequestTargetUrlCodingStrategy class that extends
 * our {@link QueryStringSharedResourceRequestTargetUrlCodingStrategy} rather
 * than Wicket's default {@link SharedResourceRequestTargetUrlCodingStrategy}
 * request parameters as query string parameters, rather than path items.
 * Uses code taken from Wicket's {@link QueryStringUrlCodingStrategy}.
 * <p>
 * This strategy is preferable to the standard
 * MergedResourceRequestTargetUrlCodingStrategy because it means Wicket's
 * last modified timestamps will be appended as query string parameters rather
 * than additional path parameters.
 * <p>
 * I.e. resource will mount at
 * {@code layout.css?w:lm=123456789} rather than
 * {@code layout.css/w:lm/123456789}.
 */
class QueryStringMergedResourceRequestTargetUrlCodingStrategy extends QueryStringSharedResourceRequestTargetUrlCodingStrategy {
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

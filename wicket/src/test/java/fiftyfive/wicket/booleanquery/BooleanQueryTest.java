package fiftyfive.wicket.booleanquery;


import fiftyfive.wicket.BaseWicketTest;
import fiftyfive.wicket.FoundationApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.TagTester;

import org.junit.Assert;
import org.junit.Test;


public class BooleanQueryTest extends BaseWicketTest
{
    @Test
    public void testBooleanQueryPage()
    {
        _tester.startPage(BooleanQueryTestPage.class);
        _tester.assertRenderedPage(BooleanQueryTestPage.class);
        
        // List<TagTester> choices = getTagsByWicketId(_tester, "choice-name");
        
    }
}
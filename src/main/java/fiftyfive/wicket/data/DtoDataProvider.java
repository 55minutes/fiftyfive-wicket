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

package fiftyfive.wicket.data;

import java.io.Serializable;
import java.util.Iterator;

import fiftyfive.util.Assert;
import fiftyfive.util.ReflectUtils;
import org.apache.wicket.markup.repeater.AbstractPageableView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * An IDataProvider that implements the DTO pattern. Suitable for full-text
 * search results and other result sets where size and data are returned in a
 * single DTO.
 * <p>
 * This is a drop-in replacement for {@link IDataProvider}, allowing you to use
 * Wicket's existing components like
 * {@link org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView DataGridView},
 * {@link org.apache.wicket.markup.repeater.data.DataView DataView},
 * {@link org.apache.wicket.markup.html.navigation.paging.PagingNavigator PagingNavigator},
 * etc. in a DTO-style efficient manner without any customization.
 * (However see the cautionary note below.)
 * <p>
 * The main advantage of using this class is that it implements
 * {@link IDataProvider#size IDataProvider.size()} and
 * {@link IDataProvider#iterator IDataProvider.iterator()}
 * with a single backend query. This is accomplished by maintaining
 * a reference to a pageable view, so that page size and offset can be
 * determined when {@code size()} is called.
 * The size is also cached to prevent
 * extra backend calls when paging links are clicked.
 * <p>
 * In other words, rather than having to issue two calls to the backend, once
 * to determine the size of the result, and then again to determine the actual
 * rows of data in the result, you can instead
 * {@link #load(int,int) implement a single load() method}
 * that returns a DTO containing both the size and the data. Often
 * times this is much more efficient, especially when dealing with web service
 * and full-text search implementations.
 * <p>
 * <b>Be sure to call {@link #flushSizeCache() flushSizeCache()} or construct
 * a new DtoDataProvider when you know your result size will
 * change, for example if the user changes her search criteria.</b>
 * <p>
 * Generic types:
 * <ul>
 * <li>{@code R} is a <b>R</b>esult DTO: a container class that holds the
 *     elements of actual data of the current page, plus a total size of the
 *     result.
 *     </li>
 * <li>{@code E} represents each <b>E</b>lement of data 
 *     in the result container.</li>
 * </ul>
 * <p>
 * Note that since DtoDataProvider needs a reference back to the pageable view
 * that is displaying its data, the object construction process takes a few
 * steps:
 * <pre class="example">
 * // Let's say this is our concrete implementation.
 * public class UserResultProvider extends DtoDataProvider&lt;UserSearchResult,User&gt;
 * {
 *     // implement iterator(UserSearchResult), size(UserSearchResult) and load(int,int)
 * }
 * 
 * // To use our provider to drive a DataView, first we construct our provider.
 * UserResultProvider provider = new UserResultProvider();
 * 
 * // Then construct the DataView, passing in our provider.
 * DataView&lt;User&gt; dataView = new DataView&lt;User&gt;("users", provider) {
 *     // implement populateItem()
 * };
 * 
 * // Finally, wire up our provider back to the view
 * provider.setPageableView(dataView);</pre>
 * <p>
 * <b>Caution: This class should be considered experimental.</b>
 * By implementing {@code size()} and {@code iterator()} with a single backend
 * query, this class goes against the Wicket developers' original intentions
 * for the IDataProvider interface. We accomplish this feat by
 * using the Java reflection API to access private and protected data within
 * {@link AbstractPageableView}.
 * 
 * @since 2.0
 */
public abstract class DtoDataProvider<R,E> implements IDataProvider<E>
{
    private transient R _transientResult;
    private transient Integer _transientOffset;
    private transient Integer _transientRowsPerPage;
    
    private boolean _loaded = false;
    private Integer _cachedDataSize;
    private AbstractPageableView _pageableView;
    
    /**
     * Constructs an empty provider. You must call
     * {@link #setPageableView setPageableView()} before
     * the provider can be used.
     */
    public DtoDataProvider()
    {
        super();
    }
    
    /**
     * Constructs a provider that will use size and offset information from
     * the specified {@code AbstractPageableView} when loading data.
     */
    public DtoDataProvider(AbstractPageableView pageableView)
    {
        super();
        this._pageableView = pageableView;
    }
    
    /**
     * Flush the cached size information that is normally held between
     * requests. This method should be called for example when your search
     * criteria changes, meaning that the result data could completely change.
     * <p>
     * You shouldn't need to use this method, since new search
     * criteria would normally mean constructing a completely new
     * DtoDataProvider.
     */
    public void flushSizeCache()
    {
        _cachedDataSize = null;
    }
    
    /**
     * Returns the pageable view associated with this provider.
     */
    public AbstractPageableView getPageableView()
    {
        return _pageableView;
    }
    
    /**
     * Sets the {@code AbstractPageableView} for which this object will be used
     * as data provider. The pageable view is consulted whenever the result
     * object is loaded from the backend, in order to get the current page
     * offset and page size. This property must not be {@code null}.
     */
    public void setPageableView(AbstractPageableView pageableView)
    {
        this._pageableView = pageableView;
    }
    
    /**
     * Loads the result object from the backend. The object will be cached
     * for the remainder of the current request, or until
     * {@link #detach() detach()} is called.
     * 
     * @param offset A zero-based offset of the first result desired, based on
     *               the current page number and page size.
     * @param amount The number of results desired (i.e. the page size).
     */
    protected abstract R load(int offset, int amount);
    
    /**
     * Returns an iterator of the items contained in the given result object.
     */
    protected abstract Iterator<? extends E> iterator(R result);
    
    /**
     * Returns the total number of items in the entire result, as represented
     * by the given result object.
     */
    protected abstract int size(R result);

    // IDataProvider support
    
    /**
     * Loads the result DTO from the backend if necessary, then delegates
     * to the implementation of {@link #iterator(Object) iterator(R)}.
     */
    public Iterator<? extends E> iterator(int offset, int amount)
    {
        // If requested offset and amount are different from when we last
        // loaded from the backend, we need to detach to force a reload.
        if(pageChanged(offset, amount))
        {
            detach();
        }
        
        return iterator(getResult());
    }
    
    /**
     * This implementation assumes the object is Serializable and simply
     * calls Model.of(). You may wish to override with a custom model.
     */
    public IModel<E> model(E object)
    {
        return (IModel<E>) Model.of((Serializable)object);
    }
    
    /**
     * Returns a cached value if possible. Otherwise loads the result DTO
     * from the backend and delegates to the implementation of
     * {@link #size(Object) size(R)}.
     */
    public int size()
    {
        if(null == _cachedDataSize)
        {
            // Force load(), which will set _cachedDataSize
            getResult();
        }
        return _cachedDataSize;
    }
    
    // loadable detachable support
    
    /**
     * Loads and returns the result DTO from the backend, or returns the
     * cached copy if it has already been loaded. The cache is discarded when
     * {@link #detach() detach()} is called.
     */
    public R getResult()
    {
        if(!_loaded)
        {
            _loaded = true;
            _transientResult = load();
            _cachedDataSize = size(_transientResult);
        }
        return _transientResult;
    }
    
    /**
     * Loads the result DTO from the backend, using the current view offset
     * and rows per page information from the pageable view associated with
     * this provider.
     */
    protected R load()
    {
        _transientOffset = getPageableViewOffset();
        _transientRowsPerPage = getPageableRowsPerPage();
        return load(_transientOffset, _transientRowsPerPage);
    }
    
    /**
     * Discards the cached view offset, rows per page, and result DTO objects.
     * Note that the result size remains cached.
     */
    public void detach()
    {
        _loaded = false;
        _transientResult = null;
        _transientOffset = null;
        _transientRowsPerPage = null;
    }
    
    // Pageable reflection "magic"
    
    /**
     * Obtains the current view offset using the Java reflection API to
     * get the {@code currentPage} private field from the pageable view and
     * multiplying it by the rows per page. 
     */
    protected int getPageableViewOffset()
    {
        assertPageableView();
        int page = (Integer) ReflectUtils.readField(
            _pageableView, "currentPage"
        );
        return page * getPageableRowsPerPage();
    }
    
    /**
     * Obtains the maximum rows per page needed by the pageable view by
     * using the Java reflection API to call the protected
     * {@link AbstractPageableView#internalGetRowsPerPage internalGetRowsPerPage()}
     * method.
     */
    protected int getPageableRowsPerPage()
    {
        assertPageableView();
        return (Integer) ReflectUtils.invokeZeroArgMethod(
            _pageableView, "internalGetRowsPerPage"
        );
    }
    
    /**
     * Returns {@code true} if the desired {@code offset} and {@code amount}
     * are different than the previously cached values.
     */
    private boolean pageChanged(int offset, int amount)
    {
        boolean changed = false;
        
        if(null == _transientOffset || null == _transientRowsPerPage)
        {
            // Data hasn't been loaded yet, so nothing has changed.
        }
        else if(_transientOffset != offset || _transientRowsPerPage < amount)
        {
            changed = true;
        }
        return changed;
    }
    
    /**
     * Asserts that {@code _pageableView} is not {@code null}.
     */
    private void assertPageableView()
    {
        Assert.notNull(
            _pageableView,
            "setPageableView() must be called before provider can load"
        );
    }
}

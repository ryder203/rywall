package de.t_ryder.rywall.ui.activities.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.Preferences
import de.t_ryder.rywall.extensions.context.findView
import de.t_ryder.rywall.extensions.utils.postDelayed
import de.t_ryder.rywall.extensions.views.gone
import de.t_ryder.rywall.extensions.views.goneIf
import de.t_ryder.rywall.extensions.views.tint
import de.t_ryder.rywall.extensions.views.visible
import de.t_ryder.rywall.ui.widgets.CleanSearchView

@Suppress("LeakingThis")
abstract class BaseSearchableActivity<out P : Preferences> : BaseFavoritesConnectedActivity<P>() {

    val toolbar: Toolbar? by findView(R.id.toolbar)

    open val initialItemId: Int = R.id.wallpapers
    var currentItemId: Int = initialItemId
        internal set

    private var searchItem: MenuItem? = null
    private var searchView: CleanSearchView? = null

    private val searchOpen: Boolean
        get() = searchView?.isOpen ?: false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(getMenuRes(), menu)

        menu?.let {
            searchItem = it.findItem(R.id.search)
            searchView = searchItem?.actionView as? CleanSearchView
            searchView?.allowKeyboardHideOnSubmit = true
            searchView?.onExpand = {
                it.findItem(R.id.search).isVisible = false
                bottomNavigation?.gone()
            }
            searchView?.onCollapse = {
                doSearch(closed = true)
                invalidateOptionsMenu()
                bottomNavigation?.visible()
            }
            searchView?.onQueryChanged = { query -> doSearch(query) }
            searchView?.onQuerySubmit = { query -> doSearch(query) }
            searchView?.bindToItem(searchItem)
            updateSearchHint()

            toolbar?.tint()
            searchItem?.isVisible = canShowSearch(currentItemId)
        }

        return super.onCreateOptionsMenu(menu)
    }

    internal fun updateSearchHint() {
        searchView?.queryHint = getSearchHint(currentItemId)
    }

    private val lock by lazy { Any() }
    private fun doSearch(filter: String = "", closed: Boolean = false) {
        try {
            synchronized(lock) { postDelayed(100) { internalDoSearch(filter, closed) } }
        } catch (e: Exception) {
        }
    }

    @MenuRes
    open fun getMenuRes(): Int = 0

    open fun getSearchHint(itemId: Int): String = ""
    open fun canShowSearch(itemId: Int): Boolean = true
    open fun internalDoSearch(filter: String = "", closed: Boolean = false) {}

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_ITEM_KEY, currentItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentItemId = savedInstanceState.getInt(CURRENT_ITEM_KEY, initialItemId)
        bottomNavigation?.selectedItemId = currentItemId
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation?.goneIf(canShowSearch(currentItemId) && searchOpen)
    }

    override fun onPause() {
        super.onPause()
        bottomNavigation?.gone()
    }

    companion object {
        private const val CURRENT_ITEM_KEY = "current_item"
    }
}
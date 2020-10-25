package de.t_ryder.rywall.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.Collection
import de.t_ryder.rywall.extensions.context.integer
import de.t_ryder.rywall.extensions.resources.lower
import de.t_ryder.rywall.ui.activities.CollectionActivity
import de.t_ryder.rywall.ui.activities.ViewerActivity
import de.t_ryder.rywall.ui.activities.base.BaseFavoritesConnectedActivity
import de.t_ryder.rywall.ui.adapters.CollectionsAdapter
import de.t_ryder.rywall.ui.fragments.base.BaseFramesFragment

open class CollectionsFragment : BaseFramesFragment<Collection>() {

    private val collectionsAdapter: CollectionsAdapter by lazy { CollectionsAdapter { onClicked(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val columnsCount = context?.integer(R.integer.collections_columns_count, 1) ?: 1
        recyclerView?.layoutManager =
            GridLayoutManager(context, columnsCount, GridLayoutManager.VERTICAL, false)
        recyclerView?.adapter = collectionsAdapter
        (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData()
    }

    override fun updateItemsInAdapter(items: ArrayList<Collection>) {
        collectionsAdapter.collections = items
    }

    override fun getFilteredItems(
        originalItems: ArrayList<Collection>,
        filter: String
    ): ArrayList<Collection> =
        ArrayList(originalItems.filter { it.name.lower().contains(filter.lower()) })

    open fun onClicked(collection: Collection) {
        startActivityForResult(
            getTargetActivityIntent()
                .apply {
                    putExtra(CollectionActivity.COLLECTION_KEY, collection)
                    putExtra(CollectionActivity.COLLECTION_NAME_KEY, collection.name)
                }, CollectionActivity.REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CollectionActivity.REQUEST_CODE &&
            resultCode == ViewerActivity.FAVORITES_MODIFIED_RESULT) {
            (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
        }
    }

    override fun loadData() {
        (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
    }

    override fun getTargetActivityIntent(): Intent =
        Intent(activity, CollectionActivity::class.java)

    override fun getEmptyText(): Int = R.string.no_collections_found

    companion object {
        const val TAG = "collections_fragment"

        @JvmStatic
        fun create(list: ArrayList<Collection> = ArrayList()) =
            CollectionsFragment().apply { updateItemsInAdapter(list) }
    }
}
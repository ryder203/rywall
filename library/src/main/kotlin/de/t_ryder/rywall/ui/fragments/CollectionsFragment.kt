package de.t_ryder.rywall.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private var openActivityLauncher : ActivityResultLauncher<Intent?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ViewerActivity.FAVORITES_MODIFIED_RESULT) {
                (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
            }
        }
    }

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
        val intent = getTargetActivityIntent()
            .apply {
                putExtra(CollectionActivity.COLLECTION_KEY, collection)
                putExtra(CollectionActivity.COLLECTION_NAME_KEY, collection.name)
            }
        try {
            openActivityLauncher?.launch(intent)
        } catch (e: Exception) {
        }
    }

    override fun loadData() {
        (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
    }

    override fun getTargetActivityIntent(): Intent =
        Intent(activity, CollectionActivity::class.java)

    override fun getEmptyText(): Int = R.string.no_collections_found
    override fun allowCheckingFirstRun(): Boolean = true

    companion object {
        const val TAG = "collections_fragment"

        @JvmStatic
        fun create(list: ArrayList<Collection> = ArrayList()) =
            CollectionsFragment().apply { updateItemsInAdapter(list) }
    }
}

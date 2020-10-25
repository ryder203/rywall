package de.t_ryder.rywall.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.Wallpaper
import de.t_ryder.rywall.extensions.context.buildTransitionOptions
import de.t_ryder.rywall.extensions.context.dimenPixelSize
import de.t_ryder.rywall.extensions.context.integer
import de.t_ryder.rywall.extensions.fragments.preferences
import de.t_ryder.rywall.extensions.frames.onClick
import de.t_ryder.rywall.extensions.frames.onFavClick
import de.t_ryder.rywall.extensions.frames.wallpapersAdapter
import de.t_ryder.rywall.extensions.resources.dpToPx
import de.t_ryder.rywall.extensions.resources.lower
import de.t_ryder.rywall.ui.activities.CollectionActivity
import de.t_ryder.rywall.ui.activities.ViewerActivity
import de.t_ryder.rywall.ui.activities.ViewerActivity.Companion.SHARED_IMAGE_NAME
import de.t_ryder.rywall.ui.activities.base.BaseFavoritesConnectedActivity
import de.t_ryder.rywall.ui.activities.base.BaseLicenseCheckerActivity
import de.t_ryder.rywall.ui.adapters.WallpapersAdapter
import de.t_ryder.rywall.ui.decorations.GridSpacingItemDecoration
import de.t_ryder.rywall.ui.fragments.base.BaseFramesFragment
import de.t_ryder.rywall.ui.viewholders.WallpaperViewHolder
import java.io.FileOutputStream

open class WallpapersFragment : BaseFramesFragment<Wallpaper>() {

    var isForFavs: Boolean = false
    open val canShowFavoritesButton: Boolean = true

    private val wallsAdapter: WallpapersAdapter by lazy {
        wallpapersAdapter(
            canShowFavoritesButton,
            (activity as? BaseFavoritesConnectedActivity<*>)?.canModifyFavorites() ?: true
        ) {
            onClick { wall, holder -> launchViewer(wall, holder) }
            onFavClick { checked, wallpaper ->
                this@WallpapersFragment.onFavClick(checked, wallpaper)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val columnsCount = context?.integer(R.integer.wallpapers_columns_count, 2) ?: 2
        recyclerView?.layoutManager =
            GridLayoutManager(context, columnsCount, GridLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(
            GridSpacingItemDecoration(
                columnsCount, context?.dimenPixelSize(R.dimen.grids_spacing, 8.dpToPx) ?: 8.dpToPx
            )
        )
        recyclerView?.adapter = wallsAdapter
        (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData()
    }

    override fun loadData() {
        (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
    }

    override fun updateItemsInAdapter(items: ArrayList<Wallpaper>) {
        wallsAdapter.wallpapers = items
    }

    override fun getFilteredItems(
        originalItems: ArrayList<Wallpaper>,
        filter: String
    ): ArrayList<Wallpaper> =
        ArrayList(originalItems.filter {
            it.name.lower().contains(filter.lower()) ||
                    it.collections.orEmpty().lower().contains(filter.lower()) ||
                    it.author.lower().contains(filter.lower())
        })

    private fun onFavClick(checked: Boolean, wallpaper: Wallpaper) {
        var updated = false
        (activity as? BaseFavoritesConnectedActivity<*>)?.let {
            if (it.canModifyFavorites()) {
                updated =
                    if (checked) it.addToFavorites(wallpaper) else it.removeFromFavorites(wallpaper)
            } else {
                it.onFavoritesLocked()
            }
        }
        if (updated) (activity as? CollectionActivity)?.setFavoritesModified()
    }

    private fun launchViewer(wallpaper: Wallpaper, holder: WallpaperViewHolder) {
        val targetIntent = getTargetActivityIntent()
        val options = if (preferences.animationsEnabled) {
            var fos: FileOutputStream? = null
            try {
                fos = activity?.openFileOutput(SHARED_IMAGE_NAME, Context.MODE_PRIVATE)
                holder.image?.drawable?.toBitmap()?.compress(Bitmap.CompressFormat.JPEG, 30, fos)
            } catch (ignored: Exception) {
            } finally {
                fos?.flush()
                fos?.close()
            }

            try {
                activity?.let {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it,
                        *(it.buildTransitionOptions(
                            // TODO: Enable image shared transition
                            arrayListOf(holder.title, holder.author) // , holder.image)
                        ))
                    )
                }
            } catch (e: Exception) {
                null
            }
        } else null
        startActivityForResult(
            targetIntent.apply {
                putExtra(
                    ViewerActivity.CAN_TOGGLE_SYSTEMUI_VISIBILITY_KEY,
                    canToggleSystemUIVisibility()
                )
                putExtra(WALLPAPER_EXTRA, wallpaper)
                putExtra(WALLPAPER_IN_FAVS_EXTRA, wallpaper.isInFavorites)
                putExtra(ViewerActivity.CURRENT_WALL_POSITION, holder.adapterPosition)
                putExtra(
                    ViewerActivity.LICENSE_CHECK_ENABLED,
                    (activity as? BaseLicenseCheckerActivity<*>)?.licenseCheckEnabled ?: false
                )
            },
            ViewerActivity.REQUEST_CODE,
            options?.toBundle()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ViewerActivity.REQUEST_CODE &&
            resultCode == ViewerActivity.FAVORITES_MODIFIED_RESULT) {
            (activity as? CollectionActivity)?.setFavoritesModified()
            (activity as? BaseFavoritesConnectedActivity<*>)?.loadWallpapersData(true)
        }
    }

    override fun getTargetActivityIntent(): Intent = Intent(activity, ViewerActivity::class.java)

    open fun notifyCanModifyFavorites(canModify: Boolean = true) {
        wallsAdapter.canModifyFavorites = canModify
        wallsAdapter.notifyDataSetChanged()
    }

    override fun getEmptyText(): Int =
        if (isForFavs) R.string.no_favorites_found else R.string.no_wallpapers_found

    override fun getEmptyDrawable(): Int =
        if (isForFavs) R.drawable.ic_empty_favorites else super.getEmptyDrawable()

    open fun canToggleSystemUIVisibility(): Boolean = true

    companion object {
        const val TAG = "wallpapers_fragment"
        const val FAVS_TAG = "favorites_fragment"
        const val IN_COLLECTION_TAG = "wallpapers_in_collection_fragment"

        internal const val WALLPAPER_EXTRA = "wallpaper"
        internal const val WALLPAPER_IN_FAVS_EXTRA = "wallpaper_in_favs"

        @JvmStatic
        fun create(
            list: ArrayList<Wallpaper> = ArrayList(),
            canModifyFavorites: Boolean = true
        ) = WallpapersFragment().apply {
            this.isForFavs = false
            notifyCanModifyFavorites(canModifyFavorites)
            updateItemsInAdapter(list)
        }

        @JvmStatic
        fun createForFavs(
            list: ArrayList<Wallpaper> = ArrayList(),
            canModifyFavorites: Boolean = true
        ) = WallpapersFragment().apply {
            this.isForFavs = true
            notifyCanModifyFavorites(canModifyFavorites)
            updateItemsInAdapter(list)
        }
    }
}
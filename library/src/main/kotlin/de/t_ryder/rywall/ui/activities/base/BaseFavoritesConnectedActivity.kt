package de.t_ryder.rywall.ui.activities.base

import android.os.Bundle
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.Preferences
import de.t_ryder.rywall.data.models.Wallpaper
import de.t_ryder.rywall.data.viewmodels.WallpapersDataViewModel
import de.t_ryder.rywall.extensions.context.string
import de.t_ryder.rywall.extensions.utils.lazyViewModel

abstract class BaseFavoritesConnectedActivity<out P : Preferences> :
    BaseSystemUIVisibilityActivity<P>() {

    open val wallpapersViewModel: WallpapersDataViewModel by lazyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldLoadFavorites() && canShowFavoritesButton())
            wallpapersViewModel.observeFavorites(this, ::onFavoritesUpdated)
    }

    internal fun addToFavorites(wallpaper: Wallpaper): Boolean {
        if (!canShowFavoritesButton()) return false
        if (canModifyFavorites()) return wallpapersViewModel.addToFavorites(wallpaper)
        onFavoritesLocked()
        return false
    }

    internal fun removeFromFavorites(wallpaper: Wallpaper): Boolean {
        if (!canShowFavoritesButton()) return false
        if (canModifyFavorites()) return wallpapersViewModel.removeFromFavorites(wallpaper)
        onFavoritesLocked()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        wallpapersViewModel.destroy(this)
    }

    internal fun loadWallpapersData(remote: Boolean = false) {
        wallpapersViewModel.loadData(
            if (remote) getDataUrl() else "",
            loadCollections = shouldLoadCollections(),
            loadFavorites = shouldLoadFavorites() && canShowFavoritesButton(),
            force = true
        )
    }

    open fun shouldLoadCollections(): Boolean = true
    open fun shouldLoadFavorites(): Boolean = true
    open fun canShowFavoritesButton(): Boolean = true
    open fun canModifyFavorites(): Boolean = true
    open fun onFavoritesLocked() {}
    open fun onFavoritesUpdated(favorites: List<Wallpaper>) {}
    open fun getDataUrl(): String = string(R.string.json_url)
}
package de.t_ryder.rywall.extensions.frames

import de.t_ryder.rywall.data.models.Wallpaper
import de.t_ryder.rywall.ui.adapters.WallpapersAdapter
import de.t_ryder.rywall.ui.viewholders.WallpaperViewHolder

internal fun wallpapersAdapter(
    canShowFavoritesButton: Boolean = true,
    canModifyFavorites: Boolean = true,
    block: WallpapersAdapter.() -> Unit
): WallpapersAdapter =
    WallpapersAdapter(canShowFavoritesButton, canModifyFavorites).apply(block)

internal fun WallpapersAdapter.onClick(what: (Wallpaper, WallpaperViewHolder) -> Unit) {
    this.onClick = what
}

internal fun WallpapersAdapter.onFavClick(what: (Boolean, Wallpaper) -> Unit) {
    this.onFavClick = what
}
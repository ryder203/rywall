package de.t_ryder.rywall.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.postDelayed
import androidx.core.widget.CompoundButtonCompat
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.Wallpaper
import de.t_ryder.rywall.extensions.context.string
import de.t_ryder.rywall.extensions.frames.buildAuthorTransitionName
import de.t_ryder.rywall.extensions.frames.buildImageTransitionName
import de.t_ryder.rywall.extensions.frames.buildTitleTransitionName
import de.t_ryder.rywall.extensions.resources.dpToPx
import de.t_ryder.rywall.extensions.resources.hasContent
import de.t_ryder.rywall.extensions.resources.tint
import de.t_ryder.rywall.extensions.resources.withAlpha
import de.t_ryder.rywall.extensions.views.context
import de.t_ryder.rywall.extensions.views.findView
import de.t_ryder.rywall.extensions.views.gone
import de.t_ryder.rywall.extensions.views.loadFramesPicResPlaceholder
import de.t_ryder.rywall.extensions.views.setPaddingTop
import de.t_ryder.rywall.extensions.views.visible
import de.t_ryder.rywall.extensions.views.visibleIf
import de.t_ryder.rywall.ui.widgets.FavoriteCheckbox
import de.t_ryder.rywall.ui.widgets.PortraitImageView

class WallpaperViewHolder(view: View) : PaletteGeneratorViewHolder(view) {
    internal val image: PortraitImageView? by view.findView(R.id.wallpaper_image)
    internal val title: TextView? by view.findView(R.id.wallpaper_name)
    internal val author: TextView? by view.findView(R.id.wallpaper_author)
    internal val favorite: FavoriteCheckbox? by view.findView(R.id.fav_button)
    private val detailsBackground: View? by view.findView(R.id.wallpaper_details_background)

    fun bind(
        wallpaper: Wallpaper,
        canShowFavoritesButton: Boolean,
        canModifyFavorites: Boolean,
        onClick: (Wallpaper, WallpaperViewHolder) -> Unit,
        onFavClick: (Boolean, Wallpaper) -> Unit
    ) {
        if (canShowFavoritesButton) {
            favorite?.setOnCheckedChangeListener(null)
            favorite?.isChecked = wallpaper.isInFavorites
            favorite?.invalidate()
            favorite?.canCheck = canModifyFavorites
            favorite?.setOnClickListener { view ->
                view.postDelayed(FAV_DELAY) {
                    onFavClick(
                        (view as? FavoriteCheckbox)?.isChecked ?: wallpaper.isInFavorites,
                        wallpaper
                    )
                }
            }
            favorite?.onDisabledClickListener = { onFavClick(wallpaper.isInFavorites, wallpaper) }
            favorite?.visible()
        } else favorite?.gone()

        title?.let {
            ViewCompat.setTransitionName(it, wallpaper.buildTitleTransitionName(adapterPosition))
        }
        author?.let {
            ViewCompat.setTransitionName(it, wallpaper.buildAuthorTransitionName(adapterPosition))
        }
        image?.let {
            ViewCompat.setTransitionName(it, wallpaper.buildImageTransitionName(adapterPosition))
        }

        title?.text = wallpaper.name
        author?.text = wallpaper.author
        author?.visibleIf(wallpaper.author.hasContent())
        itemView.setOnClickListener { onClick(wallpaper, this) }
        image?.loadFramesPicResPlaceholder(
            wallpaper.url,
            wallpaper.thumbnail,
            context.string(R.string.wallpapers_placeholder),
            onImageLoaded = generatePalette
        )
    }

    @Suppress("ConstantConditionIf")
    override fun doWithColors(bgColor: Int, textColor: Int) {
        if (GRADIENT_CENTER_ALPHA <= .5F) {
            detailsBackground?.post {
                detailsBackground?.setPaddingTop(96.dpToPx)
                image?.postDelayed(2) { updateImageColors(bgColor) }
            }
        } else updateImageColors(bgColor)
        title?.setTextColor(textColor)
        author?.setTextColor(textColor)
        favorite?.let { favBtn ->
            favBtn.buttonDrawable =
                CompoundButtonCompat.getButtonDrawable(favBtn)?.tint(textColor)
        }
    }

    private fun updateImageColors(bgColor: Int) {
        image?.setOverlayColor(bgColor.withAlpha(OVERLAY_ALPHA))
        image?.setGradientColors(
            intArrayOf(
                bgColor.withAlpha(GRADIENT_END_ALPHA),
                bgColor.withAlpha(GRADIENT_CENTER_ALPHA),
                bgColor.withAlpha(GRADIENT_START_ALPHA)
            )
        )
    }

    companion object {
        private const val FAV_DELAY = 100L
        internal const val COLORED_TILES_ALPHA = .9F
        private const val GRADIENT_START_ALPHA = .9F
        private const val GRADIENT_CENTER_ALPHA = .9F
        private const val GRADIENT_END_ALPHA = .9F
        private const val OVERLAY_ALPHA = .15F
    }
}
package de.t_ryder.rywall.ui.fragments.viewer

import android.view.View
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.Wallpaper
import de.t_ryder.rywall.extensions.resources.dpToPx
import de.t_ryder.rywall.extensions.utils.MAX_FRAMES_PALETTE_COLORS
import de.t_ryder.rywall.extensions.utils.bestSwatches
import de.t_ryder.rywall.extensions.views.findView
import de.t_ryder.rywall.extensions.views.setPaddingLeft
import de.t_ryder.rywall.extensions.views.setPaddingRight
import de.t_ryder.rywall.ui.adapters.WallpaperDetailsAdapter
import de.t_ryder.rywall.ui.decorations.DetailsGridSpacingItemDecoration
import de.t_ryder.rywall.ui.fragments.base.BaseBottomSheet
import kotlin.math.roundToInt

class DetailsFragment : BaseBottomSheet() {

    private var shouldShowPaletteDetails: Boolean = true

    var wallpaper: Wallpaper? = null
        set(value) {
            field = value
            wallpaperDetailsAdapter.wallpaper = value
            wallpaperDetailsAdapter.notifyDataSetChanged()
        }

    var palette: Palette? = null
        set(value) {
            field = value
            wallpaperDetailsAdapter.paletteSwatches = ArrayList(value?.bestSwatches.orEmpty())
        }

    private val wallpaperDetailsAdapter: WallpaperDetailsAdapter by lazy {
        WallpaperDetailsAdapter(wallpaper, shouldShowPaletteDetails)
    }

    override fun getContentView(): View? {
        val view = View.inflate(context, R.layout.fragment_recyclerview, null)

        val recyclerView: RecyclerView? by view.findView(R.id.recycler_view)
        recyclerView?.setPaddingLeft(8.dpToPx)
        recyclerView?.setPaddingRight(8.dpToPx)
        val columns = (MAX_FRAMES_PALETTE_COLORS / 2.0).roundToInt()
        val decoration = DetailsGridSpacingItemDecoration(8.dpToPx)
        val lm = GridLayoutManager(context, columns)

        recyclerView?.layoutManager = lm
        wallpaperDetailsAdapter.setLayoutManager(lm)
        recyclerView?.adapter = wallpaperDetailsAdapter
        recyclerView?.addItemDecoration(decoration)

        return view
    }

    companion object {
        @JvmStatic
        fun create(
            wallpaper: Wallpaper? = null,
            palette: Palette? = null,
            shouldShowPaletteDetails: Boolean = true
        ) =
            DetailsFragment().apply {
                this.wallpaper = wallpaper
                this.palette = palette
                this.shouldShowPaletteDetails = shouldShowPaletteDetails
            }
    }
}
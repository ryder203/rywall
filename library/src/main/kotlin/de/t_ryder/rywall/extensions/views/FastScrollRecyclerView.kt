package de.t_ryder.rywall.extensions.views

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import de.t_ryder.rywall.R
import de.t_ryder.rywall.extensions.context.color
import de.t_ryder.rywall.extensions.context.resolveColor
import de.t_ryder.rywall.extensions.resources.withAlpha

fun FastScrollRecyclerView.attachSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout?) {
    swipeRefreshLayout ?: return
    setOnFastScrollStateChangeListener(object : OnFastScrollStateChangeListener {
        override fun onFastScrollStart() {
            swipeRefreshLayout.isEnabled = false
        }

        override fun onFastScrollStop() {
            swipeRefreshLayout.isEnabled = true
        }
    })
}

fun FastScrollRecyclerView.tint() {
    val trackColor = context.resolveColor(R.attr.colorOnSurface, context.color(R.color.onSurface))
    setThumbColor(context.resolveColor(R.attr.colorAccent, context.color(R.color.accent)))
    setThumbInactiveColor(trackColor.withAlpha(.5F))
    setTrackColor(trackColor.withAlpha(.3F))
}
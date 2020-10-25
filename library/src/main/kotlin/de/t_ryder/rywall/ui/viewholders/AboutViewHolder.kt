package de.t_ryder.rywall.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.AboutItem
import de.t_ryder.rywall.extensions.resources.hasContent
import de.t_ryder.rywall.extensions.views.findView
import de.t_ryder.rywall.extensions.views.loadFramesPic
import de.t_ryder.rywall.extensions.views.visibleIf
import de.t_ryder.rywall.ui.widgets.AboutButtonsLayout

class AboutViewHolder(view: View) : SectionedViewHolder(view) {

    private val photoImageView: AppCompatImageView? by view.findView(R.id.photo)
    private val nameTextView: TextView? by view.findView(R.id.name)
    private val descriptionTextView: TextView? by view.findView(R.id.description)
    private val buttonsView: AboutButtonsLayout? by view.findView(R.id.buttons)

    fun bind(aboutItem: AboutItem?) {
        aboutItem ?: return
        nameTextView?.text = aboutItem.name
        nameTextView?.visibleIf(aboutItem.name.hasContent())
        descriptionTextView?.text = aboutItem.description
        descriptionTextView?.visibleIf(aboutItem.description.orEmpty().hasContent())
        aboutItem.links.forEach { buttonsView?.addButton(it.first, it.second) }
        buttonsView?.visibleIf(aboutItem.links.isNotEmpty())
        photoImageView?.loadFramesPic(aboutItem.photoUrl.orEmpty(), cropAsCircle = true)
    }
}
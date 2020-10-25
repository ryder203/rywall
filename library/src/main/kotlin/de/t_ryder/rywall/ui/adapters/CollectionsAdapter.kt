package de.t_ryder.rywall.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.models.Collection
import de.t_ryder.rywall.extensions.context.boolean
import de.t_ryder.rywall.extensions.views.inflate
import de.t_ryder.rywall.ui.viewholders.CollectionViewHolder

class CollectionsAdapter(private val onClick: ((collection: Collection) -> Unit)? = null) :
    RecyclerView.Adapter<CollectionViewHolder>() {

    var collections: ArrayList<Collection> = ArrayList()
        set(value) {
            collections.clear()
            collections.addAll(value)
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(collections[position], onClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val shouldBeFilled = parent.context.boolean(R.bool.enable_filled_collection_preview)
        return CollectionViewHolder(
            parent.inflate(
                if (shouldBeFilled) R.layout.item_collection_filled
                else R.layout.item_collection
            )
        )
    }

    override fun getItemCount(): Int = collections.size
}
package de.t_ryder.rywall.extensions.views

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

val RecyclerView.ViewHolder.context: Context
    get() = itemView.context
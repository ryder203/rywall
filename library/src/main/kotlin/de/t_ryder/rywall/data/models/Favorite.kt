package de.t_ryder.rywall.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(@PrimaryKey val url: String = "")
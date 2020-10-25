package de.t_ryder.rywall.data.network

import de.t_ryder.rywall.data.models.Wallpaper
import retrofit2.http.GET
import retrofit2.http.Url

interface WallpapersJSONService {
    @GET
    suspend fun getJSON(@Url url: String): List<Wallpaper>
}
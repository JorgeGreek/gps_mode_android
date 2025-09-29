package com.cliomuse.gps_mode


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    companion object {
        const val CONTENT_TYPE = "Content-Type: application/json"
    }


    @Headers(CONTENT_TYPE)
    @GET("directions/v5/mapbox/walking/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String,
        @Query("alternatives") alternatives: Boolean,
        @Query("continue_straight") continueStraight: Boolean,
        @Query("geometries") geometries: String,
        @Query("overview") overview: String,
        @Query("steps") steps: Boolean,
        @Query("access_token") accessToken: String
    ): Response<DirectionsResponse>
}


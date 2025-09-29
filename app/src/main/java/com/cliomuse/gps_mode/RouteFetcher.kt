package com.cliomuse.gps_mode

import android.util.Log
import com.liulishuo.okdownload.OkDownloadProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class RouteFetcher( ) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.mapbox.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mapboxApi: ApiService = retrofit.create(ApiService::class.java)


  fun fetchRoute(
       tourPoints: List<Point>,
       tourId: Int
    ): Flow<Result<DirectionsResponse.Geometry?>> = flow {
        val coordinatesString = tourPoints.joinToString(";") { "${it.lon},${it.lat}" }
        val accessToken = OkDownloadProvider.context.getString(R.string.mapbox_key)

       Log.d("RouteFetcher", "Fetching route for tour ID: $tourId")

        val response: Response<DirectionsResponse> = mapboxApi.getRoute(
            coordinates = coordinatesString,
            alternatives = false,
            continueStraight = true,
            geometries = "geojson",
            overview = "full",
            steps = false,
            accessToken = accessToken
        )

        val result: Result<DirectionsResponse.Geometry?> = response.safeCall(
            transform = { directionsResponse ->
                directionsResponse.routes.firstOrNull()?.geometry
            },
            errorFactory = FailureFactory()
        )

        emit(result)
    }.catch { exception ->
        emit(FailureFactory<DirectionsResponse.Geometry?>().handleException(exception))
       Log.e("RouteFetcher", "Failed to fetch route for tour ID $tourId: ${exception.message}")
    }
}


fun <T, R> Response<T>.safeCall(
    transform: (T) -> R,
    errorFactory: FailureFactory<R> = FailureFactory()
): Result<R> {
    return try {
        val body = body()
        if (isSuccessful && body != null) {
            Result.success(transform(body))
        } else {
            errorFactory.handleCode(code = code(), errorBody = errorBody())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


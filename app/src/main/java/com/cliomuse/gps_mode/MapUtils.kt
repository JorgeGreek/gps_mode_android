package com.cliomuse.gps_mode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Parcelable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


fun loadAnnotations(
    context: Context,
    mapView: MapView,
    itemsList: List<Item>,
    onAnnotationClick: (Item) -> Unit
) {
    val pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

    itemsList.forEachIndexed { index, item ->
        if (item.lat != null && item.lat!!.isNotEmpty() && item.lon != null && item.lon!!.isNotEmpty()) {
            val iconBitmap = createIconWithIndex(context, index + 1)

            val annotationOptions = PointAnnotationOptions()
                .withPoint(
                    com.mapbox.geojson.Point.fromLngLat(
                        item.lon!!.toDouble(),
                        item.lat!!.toDouble()
                    )
                )
                .withIconImage(iconBitmap)
                .withIconAnchor(IconAnchor.BOTTOM)
                .withData(JsonParser.parseString(Gson().toJson(item)))

            val annotation = pointAnnotationManager.create(annotationOptions)

            pointAnnotationManager.addClickListener {
                val clickedItem = Gson().fromJson(it.getData().toString(), Item::class.java)
                onAnnotationClick(clickedItem)
                true
            }
        }
    }

    pointAnnotationManager.iconAllowOverlap = true
}

fun createIconWithIndex(context: Context, index: Int): Bitmap {
    val primary = Color(0xFFE35056)
    val tintInt: Int? = primary?.toArgb()

    val drawable = ContextCompat.getDrawable(
        context,
        R.drawable.ic_baseline_location_filled_on_24
    )?.mutate() ?: return createBitmap(90, 90)

    if (tintInt != null) {
        DrawableCompat.setTint(drawable, tintInt)
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
    }

    val baseIcon = drawable.toBitmap(90, 90)

    val bitmap = createBitmap(90, 90)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    canvas.drawBitmap(baseIcon, 0f, 0f, paint)

    paint.color = Color.White.toArgb()
    paint.textSize = 28f
    paint.isAntiAlias = true
    paint.textAlign = Paint.Align.CENTER

    val xPos = canvas.width / 2
    val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

    canvas.drawText(index.toString(), xPos.toFloat(), yPos, paint)

    return bitmap
}


fun loadStartFinishPointIcons(
    context: Context,
    style: Style,
    startingPoint: StartingPoint?,
    finishingPoint: StartingPoint?,
    loadStartFinishPointIconsFlag: Boolean
) {
    if (!loadStartFinishPointIconsFlag) return

    val startingPointCoordinates = startingPoint?.coordinates?.let { coords ->
        val lon = coords.lon
        val lat = coords.lat
        if (!lon.isNullOrBlank() && !lat.isNullOrBlank()) {
            com.mapbox.geojson.Point.fromLngLat(lon.toDouble(), lat.toDouble())
        } else null
    }

    val finishingPointCoordinates = finishingPoint?.coordinates?.let { coords ->
        if (coords.lon != startingPoint?.coordinates?.lon || coords.lat != startingPoint?.coordinates?.lat) {
            val lon = coords.lon
            val lat = coords.lat
            if (!lon.isNullOrBlank() && !lat.isNullOrBlank()) {
                com.mapbox.geojson.Point.fromLngLat(lon.toDouble(), lat.toDouble())
            } else null
        } else null
    }

    fun getBitmapFromVectorDrawable(
        context: Context,
        drawableId: Int,
        tint: Int? = null
    ): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate()
            ?: error("Drawable not found")
        if (tint != null) {
            DrawableCompat.setTint(drawable, tint)
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
        }
        val w = drawable.intrinsicWidth.coerceAtLeast(1)
        val h = drawable.intrinsicHeight.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    val primary = Color(0xFFE35056)
    val useMono = primary != null

    val startDrawableId = if (useMono) {
        R.drawable.start_icon_mono
    } else {
        R.drawable.start_icon
    }

    val startTintInt: Int? = primary?.toArgb()

    val startBitmap = getBitmapFromVectorDrawable(
        context = context,
        drawableId = startDrawableId,
        tint = startTintInt
    )
    val startBitmapScaled = Bitmap.createScaledBitmap(startBitmap, 60, 60, /* filter = */ false)
    style.addImage("marker-starting", startBitmapScaled)

    if (finishingPointCoordinates != null) {
        val finishBitmap = getBitmapFromVectorDrawable(context, R.drawable.finish_icon)
        val finishBitmapScaled = Bitmap.createScaledBitmap(finishBitmap, 60, 60, false)
        style.addImage("marker-finishing", finishBitmapScaled)
    }

    finishingPointCoordinates?.let {
        val source = GeoJsonSource.Builder("marker-source-finishing")
            .feature(Feature.fromGeometry(it))
            .build()
        style.addSource(source)

        val layer = SymbolLayer("marker-layer-finishing", "marker-source-finishing")
            .iconImage("marker-finishing")
            .iconSize(1.3)
        style.addLayer(layer)
    }

    startingPointCoordinates?.let {
        val source = GeoJsonSource.Builder("marker-source-starting")
            .feature(Feature.fromGeometry(it))
            .build()
        style.addSource(source)

        val layer = SymbolLayer("marker-layer-starting", "marker-source-starting")
            .iconImage("marker-starting")
            .iconSize(1.3)
        style.addLayer(layer)
    }
}

fun getRoute(context: Context, mapboxMap: MapboxMap, tourId: Int, pointList: List<Point>?) {
    val routeManager = RouteManager(context)
    val chunksCount = routeManager.getChunksCount(tourId)
    val routes = routeManager.readRouteChunksFromFile(tourId, chunksCount)

    if (routes.isNotEmpty()) {
        routes.forEach { route ->
            CoroutineScope(Dispatchers.Main).launch {
                drawRoute(route, mapboxMap)
            }
        }
    } else {
        val routeFetcher =
            RouteFetcher()
        val chunks = pointList?.chunked(24)

        chunks?.forEachIndexed { index, chunk ->
            CoroutineScope(Dispatchers.IO).launch {
                routeFetcher.fetchRoute(chunk, tourId).collect { result ->
                    result.onSuccess { route ->
                        route?.let {
                            withContext(Dispatchers.Main) {
                                drawRoute(it, mapboxMap)
                                Log.e(
                                    "Route",
                                    "Route drawn successfully for chunk $index of tour ID $tourId"
                                )
                            }

                            routeManager.saveRouteToFile(it, tourId, index, chunk.size)
                        }
                    }.onFailure { exception ->
                        Log.e(
                            "Route",
                            "Failed to fetch route for chunk $index of tour ID $tourId: ${exception.message}"
                        )
                    }
                }
            }
        }
    }
}

fun drawOfflineRoute(
    context: Context,
    mapboxMap: MapboxMap,
    tourId: Int
) {
    val routeManager = RouteManager(context)
    val chunksCount  = routeManager.getChunksCount(tourId)
    val geometries   = routeManager.readRouteChunksFromFile(tourId, chunksCount)
    geometries.forEach { geom ->
        drawRoute(geom, mapboxMap)
    }
}

fun drawRoute(geometry: DirectionsResponse.Geometry, mapboxMap: MapboxMap) {
    val points = geometry.coordinates.map { com.mapbox.geojson.Point.fromLngLat(it[0], it[1]) }
    val lineString = LineString.fromLngLats(points)
    mapboxMap.getStyle { style ->
        val uniqueSourceId =
            "route_source_${System.currentTimeMillis()}"
        val uniqueLayerId = "route_layer_${System.currentTimeMillis()}"

        if (style.getSourceAs<GeoJsonSource>(uniqueSourceId) != null) {
            style.removeStyleLayer(uniqueSourceId)
        }
        if (style.getLayer(uniqueLayerId) != null) {
            style.removeStyleLayer(uniqueLayerId)
        }

        style.addSource(geoJsonSource(uniqueSourceId) {
            geometry(lineString)
        })

        style.addLayer(lineLayer(uniqueLayerId, uniqueSourceId) {
            lineCap(LineCap.ROUND)
            lineJoin(LineJoin.ROUND)
            lineWidth(5.0)
            lineColor("#0077ff")
            lineOpacity(0.5)
        })
    }
}

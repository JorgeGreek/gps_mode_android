package com.cliomuse.gps_mode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cliomuse.gps_mode.ui.theme.Gps_modeTheme
import com.liulishuo.okdownload.OkDownloadProvider
import com.mapbox.common.MapboxOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gps_modeTheme {
                val tour = createDemoGPSTour()
                FullScreenMapView(tour = tour)
            }
        }
    }
}


@Composable
fun FullScreenMapView(tour: Tour) {
    val accessToken = OkDownloadProvider.context.getString(R.string.mapbox_key)
    MapboxOptions.accessToken = accessToken

    MapboxView(
        modifier = Modifier.fillMaxSize(),
        mapBounds = tour.mapBounds,
        startingPoint = tour.startingPoint,
        finishingPoint = tour.finishingPoint,
        itemsList = tour.items ?: listOf(),
        pointList = tour.points,
        tour = tour,
        customMapStyle = tour.mapStyle ?: "",
        showRoute = tour.showRoute ?: true,
        loadAnnotationsFlag = true,
        loadStartFinishPointIconsFlag = true,
        loadRouteFlag = true,
        applyZoom = true,
        show3DButton = true,
        showLocationButton = true,
        openGoogleMaps = true,
        loadCustomMapStyle = false,
        showPuck = false,
        onAnnotationClick = { }
    )
}

@Composable
fun MapSection(
    mapBounds: MapBounds?,
    finishingPoint: StartingPoint?,
    startingPoint: StartingPoint?,
    itemsList: List<Item>,
    pointsList: List<Point>?,
    tour: Tour,
    customMapStyle: String,
    showRoute: Boolean,
    onMapListener: (Item) -> Unit
) {
    val accessToken = OkDownloadProvider.context.getString(R.string.mapbox_key)
    MapboxOptions.accessToken = accessToken

    val hasCoordsStart = startingPoint?.coordinates?.let { coordinates ->
        !coordinates.lat.isNullOrBlank() && !coordinates.lon.isNullOrBlank()
    } ?: false

    val hasCoordsFinish = finishingPoint?.coordinates?.let { coordinates ->
        !coordinates.lat.isNullOrBlank() && !coordinates.lon.isNullOrBlank()
    } ?: false

    val hasLabelStart = startingPoint?.let {
        !it.name.isNullOrBlank() || !it.address.isNullOrBlank()
    } ?: false

    val hasLabelFinish = finishingPoint?.let {
        !it.name.isNullOrBlank() || !it.address.isNullOrBlank()
    } ?: false

    val isDistinct = hasCoordsStart && hasCoordsFinish && run {
        val start = startingPoint?.coordinates
        val finish = finishingPoint?.coordinates
        start?.lat != finish?.lat || start?.lon != finish?.lon
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCoordsStart) {
            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.starting_point),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFE35056)
                )
            )

            Spacer(Modifier.height(24.dp))

            MapboxView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                mapBounds = mapBounds,
                startingPoint = startingPoint,
                finishingPoint = if (hasCoordsFinish) finishingPoint else null,
                itemsList = itemsList,
                loadAnnotationsFlag = true,
                loadStartFinishPointIconsFlag = true,
                pointList = pointsList,
                loadRouteFlag = true,
                applyZoom = true,
                onAnnotationClick = { onMapListener(it) },
                loadCustomMapStyle = false,
                show3DButton = true,
                openGoogleMaps = true,
                customMapStyle = customMapStyle,
                showRoute = showRoute,
                tour = tour,
                showLocationButton = true,
                showPuck = false
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
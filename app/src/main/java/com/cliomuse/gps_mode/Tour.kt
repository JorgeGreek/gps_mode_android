package com.cliomuse.gps_mode


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Tour(
    @SerializedName("address")
    var address: String?,
    @SerializedName("admission")
    var admission: String?,
    @SerializedName("audioStatus")
    var audioStatus: Int?,
    @SerializedName("author")
    var author: String?,
    @SerializedName("authorDescription")
    var authorDescription: String?,
    @SerializedName("authorImage")
    var authorImage: String?,
    @SerializedName("categories")
    var categories: List<String>?,
    @SerializedName("contributors")
    var contributors: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("finishing_point")
    var finishingPoint: StartingPoint?,
    @SerializedName("finishing_point_address")
    var finishingPointAddress: String?,
    @SerializedName("finishing_point_name")
    var finishingPointName: String?,
    @SerializedName("groundImageFile")
    var groundImageFile: String?,
    @SerializedName("hasSponsor")
    var hasSponsor: Int?,
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageFile")
    var imageFile: String?,
    @SerializedName("is_featured")
    var isFeatured: Boolean?,
    @SerializedName("isIndoors")
    var isIndoors: Boolean?,
    @SerializedName("items")
    var items: List<Item>?,
    @SerializedName("langId")
    var langId: Int?,
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("mapBounds")
    var mapBounds: MapBounds?,
    @SerializedName("mapStyle")
    var mapStyle: String?,
    @SerializedName("maxZoom")
    var maxZoom: Int?,
    @SerializedName("minZoom")
    var minZoom: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("openHours")
    var openHours: String?,
    @SerializedName("points")
    var points: List<Point>?,
    @SerializedName("showRoute")
    var showRoute: Boolean?,
    @SerializedName("sponsor")
    var sponsor: String?,
    @SerializedName("sponsorImage")
    var sponsorImage: String?,
    @SerializedName("sponsorTitle")
    var sponsorTitle: String?,
    @SerializedName("sponsorWebsite")
    var sponsorWebsite: String?,
    @SerializedName("sponsorImageLink")
    var sponsorImageLink: String?,
    @SerializedName("sponsorImageSubPath")
    var sponsorImageSubPath: String?,
    @SerializedName("starting_point")
    var startingPoint: StartingPoint?,
    @SerializedName("starting_point_address")
    var startingPointAddress: String?,
    @SerializedName("starting_point_name")
    var startingPointName: String?,
    @SerializedName("stops")
    var stops: Int?,
    @SerializedName("stories")
    var stories: Int?,
    @SerializedName("telephone")
    var telephone: String?,
    @SerializedName("thumbFile")
    var thumbFile: String?,
    @SerializedName("type_id")
    var typeId: Int?,
    @SerializedName("userAccess")
    var userAccess: Boolean?,
    @SerializedName("value_id")
    var valueId: Int?,
    @SerializedName("website")
    var website: String?
): Parcelable


@Parcelize
@Serializable
data class Point(
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("sequence")
    var sequence: Int?
): Parcelable

@Parcelize
@Serializable
data class StartingPoint(
    @SerializedName("address")
    var address: String?,
    @SerializedName("coordinates")
    var coordinates: Coordinates?,
    @SerializedName("name")
    var name: String?
): Parcelable



@Parcelize
@Serializable
data class Coordinates(
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?
): Parcelable



@Parcelize
@Serializable
data class MapBounds(
    @SerializedName("northEast")
    var northEast: NorthEast?,
    @SerializedName("southWest")
    var southWest: SouthWest?
): Parcelable


@Parcelize
@Serializable
data class NorthEast(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lon")
    var lon: Double?
): Parcelable

@Parcelize
@Serializable
data class SouthWest(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lon")
    var lon: Double?
): Parcelable


@Parcelize
@Serializable
data class Item(
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageFile")
    var imageFile: String?,
    @SerializedName("index")
    var index: Int?,
    @SerializedName("langId")
    var langId: Int?,
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("lon")
    var lon: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("secret")
    var secret: String?,
    @SerializedName("stories")
    var stories: List<Story>?,
    @SerializedName("thumbFile")
    var thumbFile: String?,
    @SerializedName("thumb_landscape_filename")
    var thumbLandscapeFilename: String?
): Parcelable

@Parcelize
@Serializable
data class Story(
    @SerializedName("audioFile")
    var audioFile: String?,
    @SerializedName("body")
    var body: String?,
    @SerializedName("difficulty")
    var difficulty: String?,
    @SerializedName("duration")
    private var durationRaw: String?,
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageFile")
    var imageFile: String?,
    @SerializedName("langId")
    var langId: Int?,
    @SerializedName("sequence_id")
    var sequenceId: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("videoFile")
    var videoFile: String?,
    @SerializedName("isSelected")
    var isSelected: Boolean = false
): Parcelable {

    val duration: Long?
        get() = durationRaw
            ?.toDoubleOrNull()
            ?.toLong()
}
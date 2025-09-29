package com.cliomuse.gps_mode

/**
 * Extensión para crear tours predefinidos con puntos de interés específicos
 */

/**
 * Crea un tour con puntos de interés predefinidos para demostración GPS
 * @return Tour configurado con 5 puntos de interés específicos
 */
fun createDemoGPSTour(): Tour {
    val tourId = 1
    
    // Puntos de interés especificados
    val interestPoints = listOf(
        Point(lat = "40.25392138837509", lon = "21.55572473151707", sequence = 1),
        Point(lat = "40.253319697921796", lon = "21.555697679017985", sequence = 2),
        Point(lat = "40.25260373493492", lon = "21.55559219266461", sequence = 3),
        Point(lat = "40.25199552208786", lon = "21.555905510761445", sequence = 4),
        Point(lat = "40.24890758785545", lon = "21.55749934631836", sequence = 5)
    )
    
    // Crear items basados en los puntos de interés
    val items = interestPoints.mapIndexed { index, point ->
        Item(
            id = index + 1,
            lat = point.lat,
            lon = point.lon,
            index = index + 1,
            name = "Punto de Interés ${index + 1}",
            imageFile = null,
            thumbFile = null,
            thumbLandscapeFilename = null,
            langId = null,
            secret = null,
            stories = null
        )
    }
    
    // Punto de inicio (primer punto de la lista)
    val startingPoint = StartingPoint(
        name = "Inicio del Tour",
        address = null,
        coordinates = Coordinates(
            lat = interestPoints.first().lat,
            lon = interestPoints.first().lon
        )
    )
    
    // Punto final (último punto de la lista)
    val finishingPoint = StartingPoint(
        name = "Final del Tour",
        address = null,
        coordinates = Coordinates(
            lat = interestPoints.last().lat,
            lon = interestPoints.last().lon
        )
    )
    
    // Calcular límites del mapa basados en las coordenadas
    val latitudes = interestPoints.mapNotNull { it.lat?.toDoubleOrNull() }
    val longitudes = interestPoints.mapNotNull { it.lon?.toDoubleOrNull() }
    
    val mapBounds = if (latitudes.isNotEmpty() && longitudes.isNotEmpty()) {
        MapBounds(
            northEast = NorthEast(
                lat = latitudes.maxOrNull(),
                lon = longitudes.maxOrNull()
            ),
            southWest = SouthWest(
                lat = latitudes.minOrNull(),
                lon = longitudes.minOrNull()
            )
        )
    } else null
    
    return Tour(
        id = tourId,
        name = "Tour GPS Demo",
        description = "Tour de demostración con puntos de interés específicos",
        points = interestPoints,
        items = items,
        startingPoint = startingPoint,
        finishingPoint = finishingPoint,
        mapBounds = mapBounds,
        showRoute = true,
        stops = interestPoints.size,
        // Centro del mapa (punto medio aproximado)
        lat = latitudes.average().toString(),
        lon = longitudes.average().toString(),
        // Resto de campos como null según requerimientos
        address = null,
        admission = null,
        audioStatus = null,
        author = null,
        authorDescription = null,
        authorImage = null,
        categories = null,
        contributors = null,
        email = null,
        finishingPointAddress = null,
        finishingPointName = null,
        groundImageFile = null,
        hasSponsor = null,
        imageFile = null,
        isFeatured = null,
        isIndoors = null,
        langId = null,
        mapStyle = null,
        maxZoom = null,
        minZoom = null,
        openHours = null,
        sponsor = null,
        sponsorImage = null,
        sponsorTitle = null,
        sponsorWebsite = null,
        sponsorImageLink = null,
        sponsorImageSubPath = null,
        startingPointAddress = null,
        startingPointName = null,
        stories = null,
        telephone = null,
        thumbFile = null,
        typeId = null,
        userAccess = null,
        valueId = null,
        website = null
    )
}

/**
 * Extensión para obtener las coordenadas centrales del tour
 */
fun Tour.getCenterCoordinates(): Pair<Double, Double>? {
    val points = this.points ?: return null
    val latitudes = points.mapNotNull { it.lat?.toDoubleOrNull() }
    val longitudes = points.mapNotNull { it.lon?.toDoubleOrNull() }
    
    return if (latitudes.isNotEmpty() && longitudes.isNotEmpty()) {
        Pair(latitudes.average(), longitudes.average())
    } else null
}

/**
 * Extensión para obtener todos los puntos como lista de coordenadas
 */
fun Tour.getAllCoordinates(): List<Pair<Double, Double>> {
    return this.points?.mapNotNull { point ->
        val lat = point.lat?.toDoubleOrNull()
        val lon = point.lon?.toDoubleOrNull()
        if (lat != null && lon != null) Pair(lat, lon) else null
    } ?: emptyList()
}


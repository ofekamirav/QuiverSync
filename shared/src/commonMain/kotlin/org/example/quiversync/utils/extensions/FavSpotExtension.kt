//package org.example.quiversync.utils.extensions
//
//import org.example.quiversync.FavSpotEntity
//import org.example.quiversync.domain.model.FavoriteSpot
//import org.example.quiversync.domain.model.Prediction.WeeklyPrediction
//
//fun FavSpotEntity.toFavSpot() = FavoriteSpot(
//    id = id.toString(),
//    ownerId = ownerId ?: "",
//    name = name,
//    location = location ?: "",
//    latitude = latitude,
//    longitude = longitude,
//    recommendedBoardId = recommendedBoardId ?: "",
//    confidence = confidence?.toInt() ?: 0,
//    waveHeight = waveHeight ?: "",
//    addedDate = addedDate ?: getTodayDateString()
//)
//

//package org.example.quiversync.features.spots
//
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import org.example.quiversync.domain.model.FavoriteSpot
//import org.example.quiversync.domain.model.FavoriteSpots
//import org.example.quiversync.domain.model.Prediction.GeminiPrediction
//import org.example.quiversync.domain.model.Prediction.WeeklyPrediction
//import org.example.quiversync.domain.model.Surfboard
//import org.example.quiversync.domain.model.forecast.WeeklyForecast
//import org.example.quiversync.features.BaseViewModel
//
//class FavSpotsViewModel(
//    private val forecastUseCases: ForecastUseCases
//): BaseViewModel() {
//
//    private val _uiState = MutableStateFlow<FavSpotsState>(FavSpotsState.Loading)
//    val uiState: StateFlow<FavSpotsState> get() = _uiState
//
//    init {
//        fetchFavSpots()
//    }
//    private fun fetchFavSpots(){
//        scope.launch {
//            val result = forecastUseCases.getWeeklyForecastBySpotUseCase(
//                latitude = 21.3069, // Example latitude for North Shore, HI
//                longitude = -157.8583 // Example longitude for North Shore, HI
//            )
//            if (result.isSuccess){
//                result.onSuccess {
//                    println(
//                        "Successfully fetched weekly forecast: ${it.list} days of data"
//                    )
//                }
//                result.onSuccess {
//                    weeklyForecast ->
//                    val favSpots = createMockSpots(weeklyForecast)
//                    _uiState.value = FavSpotsState.Loaded(favSpots)
//                }
//            }
//            else{
//                _uiState.value = FavSpotsState.Error(result.exceptionOrNull()?.message ?: "Failed to fetch favorite spots")
//            }
//        }
//    }
//}
//
//private fun createMockSpots(weeklyForecast: WeeklyForecast): FavoriteSpots {
//    val favSpots = listOf(
//        FavoriteSpot("Pipeline", "North Shore, HI", "Pipeline", "94", 3.2,3.1,"1" , 2 , "d" , "4"),
//        FavoriteSpot("Punta Roca", "North Shore, HI", "Punta Roca", "94", 3.2,3.1,"2" , 2 , "d" , "4"),
//        FavoriteSpot("Trestles", "North Shore, HI", "Trestles", "94", 3.2,3.1,"1" , 2 , "d" , "4")
//    )
//
//    return FavoriteSpots(items = favSpots)
//}
//
//
//
//
//
//private fun createMockPrediction(weeklyForecast : WeeklyForecast): WeeklyPrediction {
//    val boards = listOf(
//        Surfboard(
//            id = "1",
//            ownerId = "",
//            model = "Holly Grail",
//            company = "Hayden Shapes",
//            type = "Shortboard",
//            imageRes = "hs_shortboard",
//            height = "6'2\"",
//            volume = "32L",
//            width = "19\"",
//            addedDate = "2024-05-01",
//            isRentalPublished = false,
//            isRentalAvailable = false
//        ),
//        Surfboard(
//            id = "2",
//            ownerId = "",
//            model = "FRK+",
//            company = "Slater Designs",
//            type = "Funboard",
//            imageRes = "hs_shortboard",
//            height = "5'8\"",
//            volume = "28L",
//            width = "20 1/4\"",
//            addedDate = "2024-04-15",
//            isRentalPublished = true,
//            isRentalAvailable = true,
//            pricePerDay = 10.0
//        )
//    )
//
//
//
//
//    val predictions = weeklyForecast.list.mapIndexed() { index, forecast ->
//        GeminiPrediction(
//            DailyForecast = forecast.,
//            Surfboard = boards[index % boards.size], // Alternate boards
//            confidence = 50 + (index * 8) % 50 // Fake confidence between 50–98
//        )
//    }
//
//    return WeeklyPrediction(List = predictions)
//}

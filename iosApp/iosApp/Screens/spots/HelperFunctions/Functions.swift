//
//  Functions.swift
//  iosApp
//
//  Created by gal levi on 09/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import Foundation
import SwiftUI
import Shared

func getDailyPredictions(
    spot: FavoriteSpot,
    allSpotsDailyPredictions: [GeminiPrediction],
    boards: [Surfboard],
    forecasts: [DailyForecast]
) -> (prediction: GeminiPrediction, surfboard: Surfboard, dailyForecast: DailyForecast)? {
    
    guard let prediction = allSpotsDailyPredictions.first(where: {
        $0.forecastLatitude == spot.spotLatitude &&
        $0.forecastLongitude == spot.spotLongitude
    }) else {
        return nil
    }
    
    guard let surfboard = getBoardFromPrediction(prediction: prediction, boards: boards) else {
        return nil
    }
    
    guard let dailyForecast = getDailyForecastFromPrediction(prediction: prediction, forecasts: forecasts) else {
        return nil
    }
    
    return (prediction, surfboard, dailyForecast)
}


func getBoardFromPrediction(prediction: GeminiPrediction, boards: [Surfboard]) -> Surfboard? {
    return boards.first { board in
        board.id == prediction.surfboardID
    }
}

func getDailyForecastFromPrediction(prediction: GeminiPrediction, forecasts: [DailyForecast]) -> DailyForecast? {
    return forecasts.first { forecast in
        forecast.latitude == prediction.forecastLatitude &&
        forecast.longitude == prediction.forecastLongitude &&
        forecast.date == prediction.date
        }
}





func getWeeklyPredictionsForecastsAndboards(spot : FavoriteSpot , weeklyForecast: [DailyForecast] , weeklyPredictions: [GeminiPrediction], boards: [Surfboard]) -> [
    (prediction: GeminiPrediction, surfboard: Surfboard, dailyForecast: DailyForecast)
]{
    print("Weekly Predictions Count: \(weeklyPredictions)")
    print("Weekly Forecast Count: \(weeklyForecast)")
    print("Boards Count: \(boards)")
    print("Spot: \(spot)")
    var weeklyPredictionsWithForecastsAndBoards: [(prediction: GeminiPrediction, surfboard: Surfboard, dailyForecast: DailyForecast)] = []

    for prediction in weeklyPredictions {
        if let surfboard = getBoardFromPrediction(prediction: prediction, boards: boards),
           let dailyForecast = weeklyForecast.first(where: { forecast in
               forecast.latitude == prediction.forecastLatitude &&
               forecast.longitude == prediction.forecastLongitude
           }) {
            weeklyPredictionsWithForecastsAndBoards.append((prediction, surfboard, dailyForecast))
        }
    }
    
    print("Weekly Predictions Count: \(weeklyPredictionsWithForecastsAndBoards)")

    return weeklyPredictionsWithForecastsAndBoards

           
}
    


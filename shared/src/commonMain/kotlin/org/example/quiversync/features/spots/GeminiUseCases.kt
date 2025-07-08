package org.example.quiversync.features.spots

import org.example.quiversync.domain.usecase.gemini.GetBestBoardForSingleDayUseCase
import org.example.quiversync.domain.usecase.gemini.WeeklyBestMatches

class GeminiUseCases (
    val getBestBoardForTodayUseCase: GetBestBoardForSingleDayUseCase,
    val getWeeklyForecastBySpotUseCase: WeeklyBestMatches,
)
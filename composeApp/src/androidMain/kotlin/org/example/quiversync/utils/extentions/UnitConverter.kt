package org.example.quiversync.utils.extentions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object UnitConverter {
    fun msToKnots(ms: Double?): Double {
        return ms?.times(1.94384) ?: 0.0
    }
    fun metersToFeet(meters: Double?): Double {
        return meters?.times(3.28084) ?: 0.0
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeekName(dateString: String): String {
        val date = LocalDate.parse(dateString)

        val dayOfWeek = date.dayOfWeek

        return dayOfWeek.getDisplayName(TextStyle.SHORT,  Locale.ENGLISH)
    }
}
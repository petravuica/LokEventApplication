package com.example.lokeventapplication.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleEventReminder(
        context: Context,
        eventId: String,
        title: String,
        description: String,
        date: String // format: yyyy-MM-dd
    ) {
        try {
            val eventDate = LocalDate.parse(date)
            val eventMorning = eventDate.atTime(LocalTime.of(8, 0)) // 08:00
            val now = java.time.LocalDateTime.now()

            val delay = Duration.between(now, eventMorning).toMillis()
            if (delay <= 0) return // ne zakazuj ako je prošlo

            val data = Data.Builder()
                .putString("title", title)
                .putString("description", description)
                .build()

            val request = OneTimeWorkRequestBuilder<EventReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(eventId)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

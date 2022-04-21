package com.example.mailinglist.utils

import android.content.Context
import com.example.mailinglist.R
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import java.util.*

class TimeUtil {
    companion object {
        fun calculateElapsedTime(context: Context, date: Date): String {
            val dateTime = DateTime(date)
            val now = DateTime()
            val period = Period(dateTime, now)

            val formatterBuilder = PeriodFormatterBuilder().appendPrefix(
                context.resources.getString(R.string.time_period_prefix) + " "
            )

            when {
                period.years == 1 -> {
                    formatterBuilder.appendYears()
                        .appendSuffix(" " + context.resources.getString(R.string.year))
                }
                period.years > 1 -> {
                    formatterBuilder.appendYears()
                        .appendSuffix(" " + context.resources.getString(R.string.years))
                }
                period.months == 1 -> {
                    formatterBuilder.appendMonths()
                        .appendSuffix(" " + context.resources.getString(R.string.month))
                }
                period.months > 1 -> {
                    formatterBuilder.appendMonths()
                        .appendSuffix(" " + context.resources.getString(R.string.months))
                }
                period.weeks == 1 -> {
                    formatterBuilder.appendWeeks()
                        .appendSuffix(" " + context.resources.getString(R.string.week))
                }
                period.weeks > 1 -> {
                    formatterBuilder.appendWeeks()
                        .appendSuffix(" " + context.resources.getString(R.string.weeks))
                }
                period.days == 1 -> {
                    formatterBuilder.appendDays()
                        .appendSuffix(" " + context.resources.getString(R.string.day))
                }
                period.days > 1 -> {
                    formatterBuilder.appendDays()
                        .appendSuffix(" " + context.resources.getString(R.string.days))
                }
                period.hours == 1 -> {
                    formatterBuilder.appendHours()
                        .appendSuffix(" " + context.resources.getString(R.string.hour))
                }
                period.hours > 1 -> {
                    formatterBuilder.appendHours()
                        .appendSuffix(" " + context.resources.getString(R.string.hours))
                }
                period.minutes == 1 -> {
                    formatterBuilder.appendMinutes()
                        .appendSuffix(" " + context.resources.getString(R.string.minute))
                }
                period.minutes > 1 -> {
                    formatterBuilder.appendMinutes()
                        .appendSuffix(" " + context.resources.getString(R.string.minutes))
                }
                period.seconds == 1 -> {
                    formatterBuilder.appendSeconds()
                        .appendSuffix(" " + context.resources.getString(R.string.second))
                }
                else -> {
                    formatterBuilder.appendSeconds()
                        .appendSuffix(" " + context.resources.getString(R.string.seconds))
                }
            }

            val formatter = formatterBuilder.printZeroNever().toFormatter()

            return period.toString(formatter)
        }
    }
}

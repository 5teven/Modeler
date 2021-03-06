package com.cout970.modeler.core.log

import com.cout970.modeler.Debugger
import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.createParentsIfNeeded
import java.io.File
import java.io.PrintStream
import java.util.*

/**
 * Created by cout970 on 2016/12/28.
 */

object Logger {

    val level get() = Config.logLevel
    val logs = File(PathConstants.LOGS_FOLDER_PATH).apply { createParentsIfNeeded(true) }
    val stream = (if (Debugger.STATIC_DEBUG) System.out else object : PrintStream(File(logs, getFileName())) {
        override fun println() {
            super.println()
            System.out.println()
        }

        override fun println(any: Any?) {
            super.println(any)
            System.out.println(any)
        }

        override fun println(any: String?) {
            super.println(any)
            System.out.println(any)
        }
    })!!

    private fun getFileName(): String {
        val time = Calendar.getInstance()
        val year = time[Calendar.YEAR]
        val month = time[Calendar.MONTH]
        val day = time[Calendar.DAY_OF_MONTH]
        val hour = time[Calendar.HOUR_OF_DAY]
        val minute = time[Calendar.MINUTE]

        val name = "log_${year}_${month + 1}_${day}_$hour-$minute.log"
        var tryNum = 0
        if (File(logs, name).exists()) {
            while (File(logs, "log_${year}_${month + 1}_${day}_$hour-${minute}_$tryNum.log").exists()) {
                tryNum++
            }
            return "log_${year}_${month + 1}_${day}_$hour-${minute}_$tryNum.log"
        }
        return name
    }
}

enum class Level(
        val priority: Int) { DEBUG(250), ERROR(1000), WARNING(500), NORMAL(250), FINE(100), FINEST(50), LOG_CLASSES(25) }


inline fun log(level: Level, func: () -> String) {
    if (level.priority >= Logger.level.priority) {
        val time = Calendar.getInstance()
        val year = time[Calendar.YEAR]
        val month = time[Calendar.MONTH]
        val day = time[Calendar.DAY_OF_MONTH]
        val hour = time[Calendar.HOUR_OF_DAY]
        val minute = time[Calendar.MINUTE]
        val second = time[Calendar.SECOND]

        var extras = ""
        if (Logger.level == Level.LOG_CLASSES) {
            val clazz = Thread.currentThread().stackTrace[1].className
            extras = "[$clazz]"
        }
        Logger.stream.println("[$year-${month + 1}-$day][$hour:$minute:$second]$extras " + func())
        Logger.stream.flush()
    }
}

fun Throwable.print() {
    printStackTrace(Logger.stream)
    Logger.stream.flush()
}
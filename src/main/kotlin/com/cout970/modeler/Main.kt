package com.cout970.modeler

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Logger
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print

/**
 * Created by cout970 on 2016/11/29.
 */

fun main(args: Array<String>) {
    log(Level.NORMAL) { "Start of log" }
    log(Level.NORMAL) { "Debug mode is " + if (Debugger.STATIC_DEBUG) "enable" else "disable" }
    log(Level.NORMAL) { "Log level: ${Logger.level}" }
    log(Level.NORMAL) { "Program arguments: '${args.joinToString()}'" }

    try {
        val init = Initializer()
        val state = init.init(args.toList())
        init.start(state)
        state.apply {
            exportManager.saveProject(PathConstants.LAST_BACKUP_FILE_PATH, projectManager)
        }
    } catch (e: kotlin.Throwable) {
        e.print()
    } finally {
        log(Level.NORMAL) { "Eng of log" }
    }

    System.exit(0)
}
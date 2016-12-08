package com.cout970.modeler.modelcontrol

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.ITickeable
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.action.HistoricalRecord
import com.cout970.modeler.modelcontrol.action.HistoryLog
import com.cout970.modeler.modelcontrol.selection.SelectionManager
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class ModelController : ITickeable {

    private val actionQueue = LinkedList<() -> Unit>()
    lateinit var eventController: EventController
    val selectionManager = SelectionManager(this)
    val clipboard = ModelClipboard(this)
    val historyLog = HistoryLog()
    val historyRecord = HistoricalRecord(historyLog, this)
    var model = Model()
    var modelUpdate: Boolean = false

    fun registerListeners(eventController: EventController) {
        this.eventController = eventController
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS && e.keycode == Keyboard.KEY_F1) {
                    historyLog.writeLog(System.out)
                }
                return false
            }
        })
    }

    fun addToQueue(function: () -> Unit) {
        actionQueue.add(function)
    }

    override fun tick() {
        while (actionQueue.isNotEmpty()) {
            actionQueue.poll().invoke()
        }
    }
}
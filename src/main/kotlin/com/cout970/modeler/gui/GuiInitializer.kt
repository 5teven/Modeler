package com.cout970.modeler.gui

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.canvas.cursor.CursorManager
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.gui.leguicomp.StringInput
import com.cout970.modeler.gui.views.EditorView
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.PropertyManager
import com.cout970.reactive.core.ReconciliationManager
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val resourceLoader: ResourceLoader,
        val timer: Timer,
        val programState: IProgramState,
        val propertyHolder: IProjectPropertiesHolder
) {

    fun init(): Gui {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }
        log(Level.FINE) { "[GuiInitializer] Creating gui resources" }
        val guiResources = GuiResources()
        log(Level.FINE) { "[GuiInitializer] Creating Editor Panel" }
        val editorPanel = EditorView()
        log(Level.FINE) { "[GuiInitializer] Creating Root Frame" }
        val root = Root(editorPanel)
        log(Level.FINE) { "[GuiInitializer] Creating CanvasContainer" }
        val canvasContainer = CanvasContainer(Panel())
        log(Level.FINE) { "[GuiInitializer] Creating CanvasManager" }
        val canvasManager = CanvasManager()
        log(Level.FINE) { "[GuiInitializer] Creating CursorManager" }
        val cursorManager = CursorManager()
        log(Level.FINE) { "[GuiInitializer] Creating Listeners" }
        val listeners = Listeners()
        log(Level.FINE) { "[GuiInitializer] Creating GuiState" }
        val guiState = GuiState(programState)
        log(Level.FINE) { "[GuiInitializer] Creating Dispatcher" }
        val dispatcher = Dispatcher()
        log(Level.FINE) { "[GuiInitializer] Creating ButtonBinder" }
        val buttonBinder = ButtonBinder(dispatcher)
        log(Level.FINE) { "[GuiInitializer] Creating KeyboardBinder" }
        val keyboardBinder = KeyboardBinder(dispatcher)
        log(Level.FINE) { "[GuiInitializer] Creating NotificationHandler" }
        val notificationHandler = NotificationHandler()
        log(Level.FINE) { "[GuiInitializer] Creating GridLines" }
        val gridLines = GridLines()
        log(Level.FINE) { "[GuiInitializer] Creating Animator" }
        val animator = Animator()
        log(Level.FINE) { "[GuiInitializer] Creating initial canvas" }
        canvasContainer.newCanvas()
        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }

        ReconciliationManager.registerMergeStrategy(TextInput::class.java, TextInputMergeStrategy)
        ReconciliationManager.registerMergeStrategy(StringInput::class.java, TextInputMergeStrategy)

        PropertyManager.setupProperties(listeners)

        return Gui(
                root = root,
                canvasContainer = canvasContainer,
                listeners = listeners,
                windowHandler = windowHandler,
                timer = timer,
                input = eventController,
                editorView = editorPanel,
                resources = guiResources,
                state = guiState,
                dispatcher = dispatcher,
                buttonBinder = buttonBinder,
                keyboardBinder = keyboardBinder,
                canvasManager = canvasManager,
                programState = programState,
                cursorManager = cursorManager,
                propertyHolder = propertyHolder,
                notificationHandler = notificationHandler,
                gridLines = gridLines,
                animator = animator
        )
    }
}
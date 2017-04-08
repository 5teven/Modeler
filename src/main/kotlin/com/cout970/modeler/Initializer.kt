package com.cout970.modeler

import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.config.ConfigManager
import com.cout970.modeler.event.EventController
import com.cout970.modeler.export.ExportManager
import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.log.print
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.resource.GuiResources
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.view.UIManager
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.window.Loop
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.vec3Of
import java.io.File

/**
 * Created by cout970 on 2016/11/29.
 */
class Initializer(val programArguments: List<String>) {

    val resourceLoader: ResourceLoader
    val windowHandler: WindowHandler
    val eventController: EventController
    val projectManager: ProjectManager
    val modelEditor: ModelEditor
    val uiManager: UIManager
    val renderManager: RenderManager
    val mainLoop: Loop
    val exportManager: ExportManager
    val guiResources: GuiResources

    init {
        Debugger.initializer = this

        log(Level.FINE) { "Loading config" }
        ConfigManager.loadConfig()
        log(Level.FINE) { "Config loaded" }

        log(Level.FINEST) { "Creating ResourceLoader" }
        resourceLoader = ResourceLoader()
        log(Level.FINEST) { "Creating WindowHandler" }
        windowHandler = WindowHandler()
        log(Level.FINEST) { "Creating EventController" }
        eventController = EventController()

        log(Level.FINEST) { "Creating ProjectManager" }
        projectManager = ProjectManager()
        log(Level.FINEST) { "Creating ModelController" }
        modelEditor = ModelEditor(projectManager)
        log(Level.FINEST) { "Creating ExportManager" }
        exportManager = ExportManager(projectManager, resourceLoader)

        log(Level.FINE) { "Loading Resources" }
        guiResources = GuiResources(resourceLoader)

        log(Level.FINEST) { "Creating RenderManager" }
        renderManager = RenderManager()
        log(Level.FINEST) { "Creating UIManager" }
        uiManager = UIManager(windowHandler, eventController, renderManager, guiResources, projectManager)


        log(Level.FINEST) { "Creating Loop" }
        mainLoop = Loop(listOf(renderManager, uiManager, eventController, modelEditor, windowHandler),
                windowHandler.timer, windowHandler::shouldClose)

        parseArgs()

        log(Level.FINE) { "Starting GLFW" }
        GLFWLoader.init()
        log(Level.FINEST) { "Starting GLFW window" }
        windowHandler.create()
        log(Level.FINEST) { "Binding listeners and callbacks to window" }
        eventController.bindWindow(windowHandler.window)

        log(Level.FINEST) { "Initializing renderers" }
        renderManager.initOpenGl(resourceLoader, mainLoop.timer, windowHandler.window)
        log(Level.FINEST) { "Registering listeners for sceneController" }
        uiManager.sceneController.registerListeners(eventController)
        log(Level.FINEST) { "Registering listeners for moduleController" }
        uiManager.moduleController.registerListeners(eventController)

        log(Level.FINE) { "Adding placeholder cube" }
        modelEditor.addCube(vec3Of(16, 16, 16))
    }

    private fun parseArgs() {
        if (programArguments.isNotEmpty()) {
            log(Level.FINE) { "Parsing arguments..." }
            if (File(programArguments[0]).exists()) {
                try {
                    log(Level.NORMAL) { "Loading Project at '${programArguments[0]}'" }
                    val project = exportManager.loadProject(programArguments[0])
                    log(Level.NORMAL) { "Project loaded" }
                    projectManager.project = project
                } catch (e: Exception) {
                    log(Level.ERROR) { "Unable to load project file at '${programArguments[0]}'" }
                    e.print()
                }
            } else {
                log(Level.ERROR) { "Invalid program argument: '${programArguments[0]}' is not a valid path to a save file" }
            }
            log(Level.FINE) { "Parsing arguments done" }
        }
    }

    fun start() {
        log(Level.FINE) { "Starting loop" }
        mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        stop()
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}
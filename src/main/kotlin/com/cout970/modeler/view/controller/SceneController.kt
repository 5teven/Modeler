package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.IInput
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.util.*
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.scene.Scene
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Vector2f

/**
 * Created by cout970 on 2016/12/27.
 */

class SceneController(val modelProvider: IModelProvider, val input: IInput, val rootFrame: Root,
                      val timer: Timer) {

    lateinit var selectedScene: Scene
    val scenes = mutableListOf<Scene>()

    var cursorCenter: IVector3 = vec3Of(0)
    var tmpModel: Model? = null

    val modelCache = Cache<Int, VAO>(1).apply { onRemove = { _, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(2).apply { onRemove = { _, v -> v.close() } }

    var transformationMode = TransformationMode.TRANSLATION

    var selectedModelAxis = SelectionAxis.NONE
    var hoveredModelAxis = SelectionAxis.NONE

    var selectedTextureAxis = SelectionAxis.NONE
    var hoveredTextureAxis = SelectionAxis.NONE

    val showAllMeshUVs = BooleanProperty(true)
    val showBoundingBoxes = BooleanProperty(false)

    val cursorTemplate: Cursor get() = Cursor(cursorCenter, transformationMode, CursorParameters.create(0.0, vec2Of(1)))

    fun tick() {
        scaleScenes()
        updateCamera()
        scenes.forEach(Scene::update)
    }

    fun updateCamera() {
        val move = Config.keyBindings.moveCamera.check(input)
        val rotate = Config.keyBindings.rotateCamera.check(input)
        if (move || rotate) {

            val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

            if (move) {
                val camera = selectedScene.camera
                val rotations = vec2Of(camera.angleY, camera.angleX).toDegrees()
                val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
                var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
                axisY *= Math.sin(rotations.y.toRads())
                var a = vec3Of(axisX.x, 0.0, axisX.y)
                var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)
                val diff = input.mouse.getMousePosDiff()

                a = a.normalize() * (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
                b = b.normalize() * (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

                selectedScene.camera = selectedScene.camera.run { copy(position = position + a + b) }
            } else if (rotate) {
                selectedScene.apply {
                    val diff = input.mouse.getMousePosDiff()
                    camera = camera.run {
                        copy(angleY = angleY + diff.xd * Config.mouseRotationSpeedX * speed)
                    }
                    camera = camera.run {
                        copy(angleX = angleX + diff.yd * Config.mouseRotationSpeedY * speed)
                    }
                }
            }
        }
    }

    fun registerListeners(eventHandler: IEventController) {

        eventHandler.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean {
                val mousePos = input.mouse.getMousePos()
                scenes.forEach { scene ->
                    if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                        scene.run {
                            val scroll = -e.offsetY * Config.cameraScrollSpeed
                            if (camera.zoom > 0.5 || scroll > 0) {
                                desiredZoom = camera.zoom + scroll * (camera.zoom / 60f)
                            }
                        }
                    }
                }
                return true
            }
        })
        eventHandler.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean {
                val mousePos = input.mouse.getMousePos()
                scenes.forEach {
                    if (mousePos.isInside(it.absolutePosition, it.size.toIVector())) {
                        selectedScene = it
                    }
                }
                scenes.any {
                    it.onEvent(e)
                }
                return false
            }
        })
        var lastOption = 0
        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS) {
                    if (Config.keyBindings.switchCameraAxis.check(e) && selectedScene is Scene3d) {
                        when (lastOption) {
                            0 -> selectedScene.camera = selectedScene.camera.copy(angleX = 0.0, angleY = 0.0)
                            1 -> selectedScene.camera = selectedScene.camera.copy(angleX = 0.0, angleY = -90.toRads())
                            2 -> selectedScene.camera = selectedScene.camera.copy(angleX = 90.toRads(), angleY = 0.0)
                            3 -> selectedScene.camera = selectedScene.camera.copy(angleX = 45.toRads(),
                                    angleY = -45.toRads())
                        }
                        lastOption++
                        if (lastOption > 3) {
                            lastOption = 0
                        }
                    } else if (Config.keyBindings.switchOrthoProjection.check(e)) {
                        (selectedScene as? Scene3d)?.apply {
                            perspective = !perspective
                        }
                    } else if (Config.keyBindings.moveCameraToCursor.check(e)) {
                        selectedScene.apply {
                            camera = camera.copy(position = -cursorCenter)
                        }
                    }
                }
                return false
            }
        })
    }

    fun getModel(model: Model): Model {
        if (tmpModel != null) {
            return tmpModel!!
        }
        return model
    }

    fun refreshScenes() {
        rootFrame.bottomCenterPanel.apply {
            clearComponents()
            for (scene in scenes) {
                addComponent(scene)
            }
        }
        selectedScene = scenes.first()
    }

    fun scaleScenes() {
        val contentPanel = rootFrame.bottomCenterPanel
        when (scenes.size) {
            1 -> scenes[0].apply {
                size = contentPanel.size
                position = Vector2f(0f, 0f)
            }
            2 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, contentPanel.size.y / 2f)
                }
            }
            3 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, contentPanel.size.y / 2f)
                }
            }
            4 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, contentPanel.size.y / 2f)
                }
                scenes[3].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, contentPanel.size.y / 2f)
                }
            }
        }
    }
}
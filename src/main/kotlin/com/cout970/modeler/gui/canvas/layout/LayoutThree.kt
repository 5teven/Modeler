package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.util.next
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class LayoutThree(override val container: CanvasContainer) : ICanvasLayout {

    var horizontalSplitter = 0.5f
    var verticalSplitter = 0.5f
    var mode: Mode = Mode.LEFT

    override fun updateCanvas() {
        when (mode) {
            Mode.LEFT -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter, container.panel.size.y)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * verticalSplitter)
                    position = Vector2f(container.panel.size.x * horizontalSplitter, 0f)
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * verticalSplitter)
                }
            }
            Mode.RIGHT -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter, container.panel.size.y)
                    position = Vector2f(container.panel.size.x * (1 - horizontalSplitter), 0f)
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * verticalSplitter)
                    position = Vector2f()
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(0f, container.panel.size.y * verticalSplitter)
                }
            }
            Mode.TOP -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * verticalSplitter)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(0f, container.panel.size.y * verticalSplitter)
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * verticalSplitter)
                }
            }
            Mode.BOTTOM -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * verticalSplitter)
                    position = Vector2f(0f, container.panel.size.y * (1 - verticalSplitter))
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f()
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter, 0f)
                }
            }
        }
    }

    override fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        Config.keyBindings.apply {
            when {
                layoutChangeMode.check(e) -> mode = mode.next()
                moveLayoutSplitterLeft.check(e) -> horizontalSplitter -= 1f / 32f
                moveLayoutSplitterRight.check(e) -> horizontalSplitter += 1f / 32f
                moveLayoutSplitterUp.check(e) -> verticalSplitter -= 1f / 32f
                moveLayoutSplitterDown.check(e) -> verticalSplitter += 1f / 32f
                newCanvas.check(e) -> {
                    container.newCanvas()
                    container.selectLayout()
                }
                deleteCanvas.check(e) -> {
                    container.removeCanvas(container.canvas.lastIndex)
                    container.selectLayout()
                }

                else -> return false
            }
        }
        gui.root.reRender()
        return true
    }

    enum class Mode {
        LEFT, RIGHT, TOP, BOTTOM
    }
}
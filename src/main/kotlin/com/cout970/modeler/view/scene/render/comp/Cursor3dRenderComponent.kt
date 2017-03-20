package com.cout970.modeler.view.scene.render.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.vertexPosSelection
import com.cout970.modeler.util.Cursor
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.scene.render.RenderContext
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */

class Cursor3dRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {

        ctx.apply {
            val cursor = scene.cursor
            val selection = selectionManager.vertexPosSelection

            if (selection != VertexPosSelection.EMPTY) {

                when (cursor.type) {
                    TransformationMode.TRANSLATION -> {
                        if (Config.enableHelperGrid && scene.perspective &&
                            sceneController.selectedModelAxis != SelectionAxis.NONE) {

                            drawHelperGrids(ctx, cursor, sceneController.selectedModelAxis)
                        }
                        renderTranslation(ctx, cursor)
                    }
                    TransformationMode.ROTATION -> {
                        renderRotation(ctx, cursor)
                    }
                    TransformationMode.SCALE -> {

                        if (Config.enableHelperGrid && scene.perspective &&
                            sceneController.selectedModelAxis != SelectionAxis.NONE) {

                            drawHelperGrids(ctx, cursor, sceneController.selectedModelAxis)
                        }
                        renderTranslation(ctx, cursor)
                    }
                }
            }
        }
    }

    fun renderTranslation(ctx: RenderContext, cursor: Cursor) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        ctx.apply {
            draw(GL11.GL_QUADS, shaderHandler.formatPC) {

                val center = cursor.center
                val radius = cursor.parameters.distanceFromCenter
                val start = radius - cursor.parameters.maxSizeOfSelectionBox / 2.0
                val end = radius + cursor.parameters.maxSizeOfSelectionBox / 2.0

                for (axis in SelectionAxis.selectedValues) {

                    val selected = ctx.sceneController.selectedModelAxis == axis ||
                                   ctx.sceneController.hoveredModelAxis == axis

                    RenderUtil.renderBar(
                            tessellator = this,
                            startPoint = center + axis.direction * start,
                            endPoint = center + axis.direction * end,
                            color = axis.direction,
                            size = if (selected)
                                cursor.parameters.minSizeOfSelectionBox * 1.5
                            else
                                cursor.parameters.minSizeOfSelectionBox
                    )
                }
            }
        }
    }

    fun renderRotation(ctx: RenderContext, cursor: Cursor) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)

        ctx.apply {
            draw(GL11.GL_QUADS, shaderHandler.formatPC) {

                //if one of the axis is selected
                if (sceneController.selectedModelAxis != SelectionAxis.NONE) {

                    val axis = sceneController.selectedModelAxis

                    RenderUtil.renderCircle(t = this,
                            center = cursor.center,
                            axis = axis,
                            radius = cursor.parameters.distanceFromCenter,
                            size = Config.cursorLinesSize * cursor.parameters.minSizeOfSelectionBox,
                            color = axis.direction)

                } else {

                    for (axis in SelectionAxis.selectedValues) {

                        RenderUtil.renderCircle(t = this,
                                center = cursor.center,
                                axis = axis,
                                radius = cursor.parameters.distanceFromCenter,
                                size = Config.cursorLinesSize * cursor.parameters.minSizeOfSelectionBox,
                                color = axis.direction)
                    }

                    val radius = cursor.parameters.distanceFromCenter

                    for (axis in SelectionAxis.selectedValues) {

                        val edgePoint = cursor.center + axis.direction * radius
                        val selected = sceneController.selectedModelAxis == axis ||
                                       sceneController.hoveredModelAxis == axis

                        RenderUtil.renderBar(tessellator = this,
                                startPoint = edgePoint - axis.rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
                                endPoint = edgePoint + axis.rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
                                color = vec3Of(1),
                                size = if (selected)
                                    cursor.parameters.minSizeOfSelectionBox * 1.5
                                else
                                    cursor.parameters.minSizeOfSelectionBox)
                    }
                }
            }
        }
    }

    private fun drawHelperGrids(ctx: RenderContext, cursor: Cursor, axis: SelectionAxis) {
        ctx.apply {
            draw(GL11.GL_LINES, shaderHandler.formatPC) {
                val grid1 = Config.colorPalette.grid1Color
                val grid2 = Config.colorPalette.grid2Color
                var col: IVector3
                val center = cursor.center

                if (axis != SelectionAxis.Y) {
                    for (x in -160..160) {
                        col = if (x % 16 == 0) grid2 else grid1
                        set(0, x, center.y, -160).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, center.y, 160).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (z in -160..160) {
                        col = if (z % 16 == 0) grid2 else grid1
                        set(0, -160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                    }
                } else {
                    for (z in -160..160) {
                        col = if (z % 16 == 0) grid2 else grid1
                        set(0, -160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (x in -160..160) {
                        col = if (x % 16 == 0) grid2 else grid1
                        set(0, x, -160, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, 160, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                }
            }
        }

    }
}
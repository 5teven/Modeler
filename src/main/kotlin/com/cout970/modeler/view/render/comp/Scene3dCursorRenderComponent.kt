package com.cout970.modeler.view.render.comp

import com.cout970.modeler.selection.*
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.render.RenderContext
import com.cout970.modeler.view.render.comp.CursorRenderer.drawCursor

/**
 * Created by cout970 on 2017/03/20.
 */

class Scene3dCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            val axis = scene.selectorCache.selectedObject as? SelectionAxis ?: SelectionAxis.NONE
            if (selectionManager.selectionMode == SelectionMode.EDIT) {
                val selection = selectionManager.vertexPosSelection
                if (selection != VertexPosSelection.EMPTY) {
                    drawCursor(scene.cursor, axis, true)
                }
            } else {
                val selection = selectionManager.elementSelection
                if (selection != ElementSelection.EMPTY) {
                    drawCursor(scene.cursor, axis, true)
                }
            }
        }
    }
}
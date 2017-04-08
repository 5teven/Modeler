package com.cout970.modeler.newView.selector

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.*
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.controller.selection.ISelectable
import com.cout970.modeler.view.controller.selection.ITranslatable
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/04/08.
 */
class Selector(val modelEditor: ModelEditor) {

    private var activeScene: Scene? = null

    fun update(contentPanel: ContentPanel, input: IInput) {

        activeScene = contentPanel.selectedScene

        activeScene?.let { activeScene ->
            val context = getMouseSpaceContext(activeScene, input.mouse.getMousePos())
            val target = activeScene.viewTarget

            val click = Config.keyBindings.selectModelControls.check(input)
            if (target.selectedObject == null) {
                updateHovered(target, context)

                val hovered = target.hoveredObject

                if (click && hovered != null) {
                    target.selectedObject = hovered
                    target.hoveredObject = null
                }
            } else if (!click) {
                target.selectedObject = null
                updateHovered(target, context)
            }
        }
    }

    fun onDrag(state: ControllerState, input: IInput, event: EventMouseDrag) {
        activeScene?.let { activeScene ->
            activeScene.viewTarget.selectedObject?.let { selectedObject ->

                if (selectedObject is ITranslatable && state.transformationMode == TransformationMode.TRANSLATION) {
                    val context = getContext(activeScene, event)
                    val model = TranslationHelper.applyTranslation(
                            model = modelEditor.model,
                            scene = activeScene,
                            obj = selectedObject,
                            input = input,
                            context = context)

                    activeScene.viewTarget.tmpModel = model
                }

            }
        }
    }

    private fun getContext(scene: Scene, event: EventMouseDrag): Pair<SceneSpaceContext, SceneSpaceContext> {
        val oldContext = getMouseSpaceContext(scene, event.oldPos)
        val newContext = getMouseSpaceContext(scene, event.newPos)
        return oldContext to newContext
    }

    private fun updateHovered(target: ViewTarget, context: SceneSpaceContext) {
        if (target.hoveredObject == null) {
            target.hoveredObject = getHoveredObject(context, target.selectableObjects)
        }
    }

    private fun getHoveredObject(ctx: SceneSpaceContext, objs: List<ISelectable>): ISelectable? {
        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, ISelectable>>()

        objs.forEach { obj ->
            val res = obj.rayTrace(ray)
            res?.let { list += it to obj }
        }

        return list.getClosest(ray)?.second
    }

    private fun getMouseSpaceContext(scene: Scene, absMousePos: IVector2): SceneSpaceContext {
        val matrix = scene.getMatrixMVP().toJOML()
        val mousePos = absMousePos - scene.absolutePosition
        val viewportSize = scene.size.toIVector()
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 0.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 1.0).toJoml3d(),
                viewport, Vector3d()).toIVector()

        val mouseRay = Ray(a, b)

        return SceneSpaceContext(mousePos, mouseRay, matrix)
    }
}
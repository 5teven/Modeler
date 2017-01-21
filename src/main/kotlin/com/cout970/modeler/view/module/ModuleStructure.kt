package com.cout970.modeler.view.module

import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.SelectionMesh
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.util.onClick
import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2016/12/31.
 */
class ModuleStructure(controller: ModuleController) : Module(controller, "Structure") {

    var hash = 0
    var openSections = mutableMapOf<ModelPath, Boolean>()

    init {
    }

    override fun tick() {
        val model = controller.modelProvider.model
        val selection = controller.modelProvider.selectionManager.selection
        val tmpHash = model.hashCode() xor selection.hashCode()

        if (tmpHash != hash) {
            hash = tmpHash
            subPanel.clearComponents()
            subPanel.size.y = 0f
            addSubComponent(Label(" Model", 3f, 0f, 172f, 20f).apply {
                textState.horizontalAlign = HorizontalAlign.LEFT
            })
            var index = 20f
            val desc = 15f

            for ((groupIndex, group) in model.groups.withIndex()) {

                val groupPath = ModelPath(groupIndex)
                val openObject = openSections[groupPath] ?: groupPath.run {
                    openSections.put(this, false)
                    false
                }

                addSubComponent(Button(if (openObject) "V" else ">", 3f, index, 20f, 20f).apply {
                    border.isEnabled = false
                }.onClick(0) {
                    openSections[groupPath] = !openSections[groupPath]!!; hash = 0
                })
                addSubComponent(Button(" ${group.name}", 25f, index, 130f, 20f).apply {
                    border.isEnabled = false
                    textState.horizontalAlign = HorizontalAlign.LEFT
                    if (selection.isSelected(groupPath)) {
                        backgroundColor.set(0f, 0.5f, 1f, 1f)
                    }
                }.onClick(0) {
                    if (controller.modelProvider.selectionManager.selectionMode == SelectionMode.GROUP) {
                        controller.modelProvider.selectionManager.run {
                            updateSelection(handleSelection(groupPath,
                                    Config.keyBindings.multipleSelection.check(controller.input)))
                        }
                    } else if (controller.modelProvider.selectionManager.selectionMode == SelectionMode.MESH) {
                        controller.modelProvider.selectionManager.run {
                            updateSelection(SelectionMesh(model.getPaths(ModelPath.Level.MESH)))
                        }
                    }
                })
                addSubComponent(Button(if (openObject) "O" else "o", 155f, index, 20f, 20f).apply {
                    border.isEnabled = false
                })
                index += 20f
                if (openObject) {
                    for (meshIndex in group.meshes.indices) {
                        val meshPath = ModelPath(groupIndex, meshIndex)
                        addSubComponent(
                                Button(" Mesh $meshIndex", 3f + desc * 2, index, 172f - desc * 2, 20f).apply {
                                    border.isEnabled = false
                                    textState.horizontalAlign = HorizontalAlign.LEFT
                                    if (selection.isSelected(meshPath)) {
                                        backgroundColor.set(0f, 0.5f, 1f, 1f)
                                    }
                                }.onClick(0) {
                                    if (controller.modelProvider.selectionManager.selectionMode == SelectionMode.MESH)
                                        controller.modelProvider.selectionManager.run {
                                            updateSelection(handleSelection(meshPath,
                                                    Config.keyBindings.multipleSelection.check(controller.input)))
                                        }
                                })
                        index += 20f
                    }
                }
            }
            if (subPanel.isEnabled) {
                maximize()
            } else {
                minimize()
            }
            controller.recalculateModules()
        }
    }
}
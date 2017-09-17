package com.cout970.modeler.controller.tasks

import com.cout970.modeler.Program
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.model.material.MaterialRef

/**
 * Created by cout970 on 2017/07/24.
 */
class TaskImportMaterial(
        val material: IMaterial
) : IUndoableTask {

    override fun run(state: Program) {
        state.projectManager.loadMaterial(material)
        state.projectManager.loadedMaterials.forEach { it.loadTexture(state.resourceLoader) }
        state.gui.editorPanel.rightPanelModule.presenter.updateObjectList()
    }

    override fun undo(state: Program) {
        if (material in state.projectManager.loadedMaterials) {
            val ref = state.projectManager.loadedMaterials.indexOf(material)
            state.projectManager.removeMaterial(MaterialRef(ref))
            state.projectManager.loadedMaterials.forEach { it.loadTexture(state.resourceLoader) }
            state.gui.editorPanel.rightPanelModule.presenter.updateObjectList()
        }
    }
}
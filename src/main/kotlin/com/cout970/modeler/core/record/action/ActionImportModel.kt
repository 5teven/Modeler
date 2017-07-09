package com.cout970.modeler.core.record.action

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import javax.swing.JOptionPane

/**
 * Created by cout970 on 2017/01/02.
 */
class ActionImportModel(
        val projectManager: ProjectManager,
        val resourceLoader: ResourceLoader,
        val path: String,
        val function: () -> IModel
) : IAction {

    override fun run() {
        try {
            val newModel = function()
            newModel.materials.forEach { it.loadTexture(resourceLoader) }
            projectManager.updateModel(newModel)
        } catch(e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error importing model: \n$e")
        }
    }

    override fun undo() = Unit

    override fun toString(): String {
        return "ActionImportModel(path='$path')"
    }
}
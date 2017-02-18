package com.cout970.modeler.modeleditor.action

import com.cout970.modeler.model.Selection
import com.cout970.modeler.modeleditor.ModelEditor

/**
 * Created by cout970 on 2016/12/08.
 */
data class ActionChangeModelSelection(val oldSelection: Selection, val newSelection: Selection,
                                      val modelEditor: ModelEditor) : IAction {

    override fun run() {
        modelEditor.selectionManager.modelSelection = newSelection
    }

    override fun undo() {
        modelEditor.selectionManager.modelSelection = oldSelection
    }

    override fun toString(): String {
        return "ActionChangeModelSelection(oldSelection=$oldSelection, newSelection=$newSelection)"
    }
}
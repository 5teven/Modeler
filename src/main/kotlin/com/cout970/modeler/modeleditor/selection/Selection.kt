package com.cout970.modeler.modeleditor.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.util.middle

/**
 * Created by cout970 on 2016/12/07.
 */
abstract class Selection {

    abstract val mode: SelectionMode
    abstract val paths: List<ModelPath>

    fun getCenter(model: Model) = paths.map { it.getCenter(model) }.middle()

    fun isSelected(path: ModelPath): Boolean {
        return paths.any { it == path }
    }

    fun containsSelectedElements(path: ModelPath): Boolean {
        return paths.any { it.compareLevel(path, path.level) }
    }
}

object SelectionNone : Selection() {

    override val mode: SelectionMode = SelectionMode.GROUP
    override val paths: List<ModelPath> = emptyList()

}

data class SelectionGroup(val group: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.GROUP
    override val paths: List<ModelPath> get() = group

}

data class SelectionMesh(val meshes: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.MESH
    override val paths: List<ModelPath> get() = meshes

}

data class SelectionQuad(val quads: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.QUAD
    override val paths: List<ModelPath> get() = quads

}

data class SelectionVertex(val vertex: List<ModelPath>) : Selection() {

    override val mode: SelectionMode = SelectionMode.VERTEX
    override val paths: List<ModelPath> get() = vertex
}
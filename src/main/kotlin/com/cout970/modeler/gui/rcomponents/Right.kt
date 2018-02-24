package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.ref
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign


data class RightPanelProps(val visible: Boolean, val modelAccessor: IModelAccessor, val state: GuiState) : RProps

class RightPanel : RStatelessComponent<RightPanelProps>() {

    override fun RBuilder.render() = div("RightPanel") {
        style {
            background { darkestColor }
            borderless()
            posY = 48f

            if (!props.visible)
                hide()
        }

        postMount {
            width = 288f
            posX = parent.width - width
            height = parent.size.y - 48f
        }

        div("Container") {

            style {
                transparent()
                borderless()
            }

            postMount {
                fillX()
                posY = 5f
                sizeY = parent.sizeY - posY
            }
            child(CreateObjectPanel::class)
            child(ModelTree::class, ModelTreeProps(props.modelAccessor))
            child(MaterialList::class, MaterialListProps(props.modelAccessor, { props.state.selectedMaterial }))
        }
    }
}

class CreateObjectPanel : RStatelessComponent<EmptyProps>() {

    override fun RBuilder.render() = div("CreateObject") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
        }

        postMount {
            marginX(5f)
            height = 64f
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Create Object"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }

            }

            postMount {
                marginX(50f)
                posY = 0f
                sizeY = 24f
            }
        }

        +IconButton("cube.template.new", "addTemplateCubeIcon", 5f, 28f, 32f, 32f).also {
            it.setTooltip("Create Template Cube")
        }
        +IconButton("cube.mesh.new", "addMeshCubeIcon", 40f, 28f, 32f, 32f).also {
            it.setTooltip("Create Cube Mesh")
        }
    }
}

data class ModelTreeProps(val modelAccessor: IModelAccessor) : RProps

class ModelTree : RStatelessComponent<ModelTreeProps>() {

    override fun RBuilder.render() = div("ModelTree") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = 300f
            posY = 70f
        }

        postMount {
            marginX(5f)
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Model Tree"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        val objs = props.modelAccessor.model.objectMap.values
        val selected = props.modelAccessor.modelSelection.map { sel ->
            { obj: IObjectRef -> sel.isSelected(obj) }
        }.getOr { false }

        objs.forEachIndexed { index, obj ->
            div(obj.name) {
                style {
                    sizeY = 24f
                    posY = 28f + index * (sizeY + 2f)
                    transparent()
                    borderless()
                    rectCorners()

                    if (selected(obj.ref)) {
                        background { lightBrightColor }
                    } else {
                        background { lightDarkColor }
                    }
                }

                postMount {
                    marginX(5f)
                }

                val icon = if (obj is IObjectCube) "obj_type_cube" else "obj_type_mesh"

                +IconButton("tree.view.select", icon, 4f, 4f, 16f, 16f).apply {
                    metadata += "ref" to obj.ref
                }
                +TextButton("tree.view.select", obj.name, 20f, 0f, 180f, 24f).apply {
                    transparent()
                    borderless()
                    fontSize = 20f
                    horizontalAlign = HorizontalAlign.LEFT
                    textState.padding.x = 5f
                    metadata += "ref" to obj.ref
                }
                +IconButton("tree.view.show.item", "showIcon", 180f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    if (obj.visible) hide() else show()
                    setTooltip("Show object")
                }
                +IconButton("tree.view.hide.item", "hideIcon", 180f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    if (!obj.visible) hide() else show()
                    setTooltip("Hide object")
                }
                +IconButton("tree.view.delete.item", "deleteIcon", 210f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to obj.ref
                    setTooltip("Delete object")
                }
            }
        }
    }
}


data class MaterialListProps(val modelAccessor: IModelAccessor, val selectedMaterial: () -> IMaterialRef) : RProps

class MaterialList : RStatelessComponent<MaterialListProps>() {

    override fun RBuilder.render() = div("MaterialList") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = 300f
            posY = 375f
        }

        postMount {
            marginX(5f)
        }

        on<EventModelUpdate> { rerender() }
        on<EventSelectionUpdate> { rerender() }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Material List"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        val model = props.modelAccessor.model
        val selection = props.modelAccessor.modelSelection
        val materialRefs = (model.materialRefs + listOf(MaterialRefNone))
        val selectedMaterial = props.selectedMaterial()

        val materialOfSelectedObjects = selection
                .map { it to it.objects }
                .map { (sel, objs) -> objs.filter(sel::isSelected) }
                .map { it.map { model.getObject(it).material } }
                .getOr(emptyList())

        materialRefs.forEachIndexed { index, ref ->
            val material = model.getMaterial(ref)

            val color = when (ref) {
                in materialOfSelectedObjects -> {
                    Config.colorPalette.greyColor.toColor()
                }
                selectedMaterial -> {
                    Config.colorPalette.brightColor.toColor()
                }
                else -> {
                    Config.colorPalette.lightDarkColor.toColor()
                }
            }

            div(material.name) {
                style {
                    sizeY = 24f
                    posY = 28f + index * (sizeY + 2f)
                    transparent()
                    borderless()
                    rectCorners()
                    backgroundColor { color }
                }

                postMount {
                    marginX(5f)
                }

                +TextButton("material.view.select", material.name, 0f, 0f, 120f, 24f).apply {
                    textState.padding.x = 10f
                    horizontalAlign = HorizontalAlign.LEFT
                    fontSize = 20f
                    transparent()
                    metadata += "ref" to material.ref
                }

                if (material.ref != MaterialRefNone) {
                    +IconButton("material.view.load", "loadMaterial", 120f, 0f, 24f, 24f).apply {
                        transparent()
                        borderless()
                        metadata += "ref" to material.ref
                        setTooltip("Load material")
                    }
                }

                +IconButton("material.view.apply", "applyMaterial", 150f, 0f, 24f, 24f).apply {
                    transparent()
                    borderless()
                    metadata += "ref" to material.ref
                    setTooltip("Apply material")
                }
            }
        }
    }
}
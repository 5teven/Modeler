package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.TinyFloatInput
import com.cout970.modeler.gui.rcomponents.TinyFloatInputProps
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getOr
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import com.cout970.vector.api.IVector3
import org.joml.Vector2f


data class EditKeyframeProps(val animator: Animator, val programState: IProgramState) : RProps

class EditKeyframe : RComponent<EditKeyframeProps, VisibleWidget>() {

    companion object {
        private const val line = 0.4f
        private const val button_line = 0.13f
    }

    val enable: Boolean get() = props.animator.selectedKeyframe != null

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditKeyframe") {
        style {
            classes("left_panel_group", "edit_animation")
            height = if (state.on) 320f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 16f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Edit Keyframe", state.on) { setState { copy(on = !on) } })

        val channelRef = props.animator.selectedChannel
        val keyframeRef = props.animator.selectedKeyframe

        val channel = props.animator.animation.channels[channelRef]
        val keyframe = keyframeRef?.let { channel?.keyframes?.getOrNull(it) }.asNullable()
        val value = keyframe.map { it.value }.getOr(TRSTransformation.IDENTITY)


        val t = value.toTRS()
        scale(t.scale)
        position(t.translation)
        rotation(t.euler.angles)

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
        onCmd("updateAnimation") { rerender() }
    }

    fun RBuilder.position(translation: IVector3) {
        div("Position") {
            style {
                height = 93f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 5f)
                }

                label("Position X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Position Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Position Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - (line + button_line))
                    fillY()
                    floatTop(6f, 5f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.xf },
                        setter = { cmd("pos.x", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.yf },
                        setter = { cmd("pos.y", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.zf },
                        setter = { cmd("pos.z", it) },
                        enabled = enable
                ))
            }

            div("RightDiv") {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * (1 - button_line)
                    width = parent.width * button_line
                    fillY()
                    floatTop(6f, 5f)
                }

                +IconButton("keyframe.spread.translate.x", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.translate.y", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.translate.z", "spread_value", 4f, 0f, 24f, 24f)
            }
        }
    }

    fun RBuilder.rotation(rotation: IVector3) {
        div("Rotation") {
            style {
                height = 93f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 5f)
                }

                label("Rotation X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Rotation Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Rotation Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - (line + button_line))
                    fillY()
                    floatTop(6f, 5f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.xf },
                        setter = { cmd("rot.x", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.yf },
                        setter = { cmd("rot.y", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.zf },
                        setter = { cmd("rot.z", it) },
                        enabled = enable
                ))
            }

            div("RightDiv") {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * (1 - button_line)
                    width = parent.width * button_line
                    fillY()
                    floatTop(6f, 5f)
                }

                +IconButton("keyframe.spread.rotation.x", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.rotation.y", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.rotation.z", "spread_value", 4f, 0f, 24f, 24f)
            }
        }
    }

    fun RBuilder.scale(scale: IVector3) {
        div("Scale") {
            style {
                height = 93f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            div("LeftDiv") {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 5f)
                }

                label("Scale X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Scale Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Scale Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div("CenterDiv") {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - (line + button_line))
                    fillY()
                    floatTop(6f, 5f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.xf },
                        setter = { cmd("size.x", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.yf },
                        setter = { cmd("size.y", it) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.zf },
                        setter = { cmd("size.z", it) },
                        enabled = enable
                ))
            }

            div("RightDiv") {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * (1 - button_line)
                    width = parent.width * button_line
                    fillY()
                    floatTop(6f, 5f)
                }

                +IconButton("keyframe.spread.scale.x", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.scale.y", "spread_value", 4f, 0f, 24f, 24f)
                +IconButton("keyframe.spread.scale.z", "spread_value", 4f, 0f, 24f, 24f)
            }
        }
    }

    fun cmd(txt: String, value: Float) {
        if (enable) {
            Panel().apply {
                metadata += mapOf("command" to txt)
                metadata += "offset" to 0f
                metadata += "content" to value.toString()
                dispatch("animation.update.keyframe")
            }
        }
    }
}
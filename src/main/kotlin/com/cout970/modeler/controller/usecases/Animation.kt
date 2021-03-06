package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.*
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.model.toTRS
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.asNullable
import com.cout970.reactive.dsl.width
import org.liquidengine.legui.component.Component
import kotlin.math.roundToInt


private var lastAnimation = 0

@UseCase("animation.add")
private fun addAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val animation = Animation.of("Animation_${lastAnimation++}")

    return TaskChain(listOf(
        TaskUpdateModel(model, model.addAnimation(animation)),
        ModifyGui { programState.selectedAnimation = animation.ref; it.animator.sendUpdate() }
    ))
}

@UseCase("animation.dup")
private fun duplicateAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val selected = programState.animation

    if (selected == AnimationNone) return TaskNone

    val animation = Animation.of(
        selected.name + "_copy",
        selected.timeLength,
        selected.channels,
        selected.channelMapping
    )

    return TaskChain(listOf(
            TaskUpdateModel(model, model.addAnimation(animation)),
            ModifyGui { programState.selectedAnimation = animation.ref; it.animator.sendUpdate() }
    ))
}

@UseCase("animation.remove")
private fun removeAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val animation = programState.selectedAnimation

    return TaskChain(listOf(
            ModifyGui { programState.selectedAnimation = AnimationRefNone; it.animator.sendUpdate() },
            TaskUpdateModel(model, model.removeAnimation(animation))
    ))
}

@UseCase("animation.channel.add")
private fun addAnimationChannel(programState: IProgramState): ITask {
    val group = programState.selectedGroup
    val selection = programState.modelSelection
    val anim = programState.animation
    val model = programState.model

    val target = if (group == RootGroupRef) {
        if (selection.isNull()) return TaskNone
        val sel = selection.getNonNull()
        if (sel.objects.size != 1) return TaskNone
        AnimationTargetObject(sel.objects.first())
    } else {
        AnimationTargetGroup(group)
    }

    val channel = Channel(
            name = "Channel ${lastAnimation++}",
            interpolation = InterpolationMethod.LINEAR,
            keyframes = listOf(
                    Keyframe(0f, target.getTransformation(model).toTRS()),
                    Keyframe(anim.timeLength, target.getTransformation(model).toTRS())
            )
    )
    val newAnimation = anim.withChannel(channel).withMapping(channel.ref, target)

    return TaskChain(listOf(
            TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation)),
            ModifyGui { it.animator.selectedChannel = channel.ref }
    ))
}


@UseCase("animation.channel.select")
private fun selectAnimationChannel(comp: Component, projectManager: ProjectManager): ITask {
    val animation = projectManager.animation
    val channel = comp.metadata["ref"] as IChannelRef

    val task1 = ModifyGui { it.animator.selectedChannel = channel }

    val target = animation.channelMapping[channel] ?: return task1

    return when (target) {
        is AnimationTargetGroup -> {
            TaskChain(listOf(task1, ModifyGui { projectManager.selectedGroup = target.ref }))
        }
        is AnimationTargetObject -> {
            val sel = projectManager.modelSelection
            val task2 = TaskUpdateModelSelection(sel, Selection.of(listOf(target.ref)).asNullable())
            TaskChain(listOf(task1, task2))
        }
    }
}

@UseCase("animation.select")
private fun selectAnimation(comp: Component, projectManager: ProjectManager): ITask = ModifyGui {
    projectManager.selectedAnimation = comp.metadata["animation"] as IAnimationRef
    it.animator.sendUpdate()
}

@UseCase("animation.channel.enable")
private fun enableAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref]!!

    val newAnimation = animation.withChannel(channel.withEnable(true))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.disable")
private fun disableAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref]!!

    val newAnimation = animation.withChannel(channel.withEnable(false))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.delete")
private fun removeAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val channel = comp.metadata["ref"] as IChannelRef
    val newAnimation = animation.removeChannels(listOf(channel))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.set.length")
private fun setAnimationLength(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val time = comp.metadata["time"] as Float

    if (time <= 0) return TaskNone

    val newAnimation = animation.withTimeLength(time)

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.panel.click")
private fun onAnimationPanelClick(comp: Component, animator: Animator, input: IInput): ITask {
    val mousePos = input.mouse.getMousePos()
    val compPos = comp.absolutePositionV
    val diffX = mousePos.xf - compPos.xf
    val diffY = mousePos.yf - compPos.yf

    val zoom = animator.zoom
    val timeToPixel = comp.width / zoom
    val pixelOffset = animator.offset * timeToPixel

    val channels = animator.animation.channels.values
    val time = (diffX - pixelOffset) / timeToPixel
    val roundTime = (time * 60f).roundToInt() / 60f

    // TODO fix incorrect bounding box
    channels.forEachIndexed { i, channel ->
        if (diffY > i * 26 && diffY <= (i + 1) * 26f) {

            channel.keyframes.forEachIndexed { index, keyframe ->
                val pos = keyframe.time * timeToPixel + pixelOffset

                if (diffX > pos - 12f && diffX <= pos + 12f) {
                    return ModifyGui {
                        animator.selectedChannel = channel.ref
                        animator.selectedKeyframe = index
                        animator.animationTime = roundTime
                        it.state.cursor.update(it)
                    }
                }
            }
        }
    }

    return ModifyGui {
        animator.selectedKeyframe = null
        animator.animationTime = roundTime
        it.state.cursor.update(it)
    }
}


@UseCase("animation.state.toggle")
private fun animationTogglePlay(): ITask = ModifyGui {
    if (it.animator.animationState == AnimationState.STOP) {
        it.animator.animationState = AnimationState.FORWARD
    } else {
        it.animator.animationState = AnimationState.STOP
    }
}

@UseCase("animation.state.backward")
private fun animationPlayBackwards(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.BACKWARD
}

@UseCase("animation.state.forward")
private fun animationPlayForward(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.FORWARD
}

@UseCase("animation.state.stop")
private fun animationStop(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.STOP
}

@UseCase("animation.seek.start")
private fun animationSeekStart(): ITask = ModifyGui {
    it.animator.animationTime = 0f
}

@UseCase("animation.seek.end")
private fun animationSeekEnd(): ITask = ModifyGui {
    it.animator.animationTime = it.programState.animation.timeLength
}

@UseCase("animation.prev.keyframe")
private fun prevKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val prev = channel.keyframes.findLast { it.time < animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = prev.time
        it.state.cursor.update(it)
    }
}

@UseCase("animation.next.keyframe")
private fun nextKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val next = channel.keyframes.find { it.time > animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = next.time
        it.state.cursor.update(it)
    }
}
package com.cout970.modeler.api.animation

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IGroupRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.TRSTransformation
import java.util.*

interface IAnimationRef {
    val id: UUID
}

sealed class AnimationTarget
data class AnimationTargetGroup(val ref: IGroupRef) : AnimationTarget()
data class AnimationTargetObject(val ref: IObjectRef) : AnimationTarget()

interface IAnimation {
    val id: UUID
    val name: String
    val channels: Map<IChannelRef, IChannel>
    val channelMapping: Map<IChannelRef, AnimationTarget>
    val timeLength: Float

    fun withChannel(channel: IChannel): IAnimation
    fun withTimeLength(newLength: Float): IAnimation
    fun withMapping(channel: IChannelRef, target: AnimationTarget): IAnimation

    fun removeChannels(list: List<IChannelRef>): IAnimation

    operator fun plus(other: IAnimation): IAnimation
}

interface IChannelRef {
    val id: UUID
}

interface IChannel {
    val id: UUID
    val name: String
    val interpolation: InterpolationMethod
    val keyframes: List<IKeyframe>
    val enabled: Boolean

    fun withName(name: String): IChannel
    fun withEnable(enabled: Boolean): IChannel
    fun withInterpolation(method: InterpolationMethod): IChannel
    fun withKeyframes(keyframes: List<IKeyframe>): IChannel
}

interface IKeyframe {
    val time: Float
    val value: TRSTransformation

    fun withValue(trs: ITransformation): IKeyframe
}

enum class InterpolationMethod {
    LINEAR, COSINE
}

enum class AnimationState {
    STOP, FORWARD, BACKWARD
}
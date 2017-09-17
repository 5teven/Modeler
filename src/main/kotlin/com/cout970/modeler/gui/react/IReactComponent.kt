package com.cout970.modeler.gui.react

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.react.leguicomp.LeguiComponentBridge
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/07.
 */
interface IReactComponent<out P, S> {

    var context: ReactContext
    var parent: Container<Component>

    val state: S
    val props: P

    fun setState(state: S)

    fun render(): Component
}

abstract class ReactComponent<out P, S : Any>(override val props: P) : IReactComponent<P, S> {

    override lateinit var context: ReactContext
    override lateinit var parent: Container<Component>

    private var _state: S? = null

    override val state: S get() = _state!!

    override fun setState(state: S) {
        if (_state != null) {
            this._state = state
            context.reRender(this)
            return
        }
        this._state = state
    }
}

class ReactContext(
        val gui: Gui,
        val root: Container<Component>,
        val func: () -> Component
) {

    fun bind() {
        gui.buttonBinder.bindButtons(root)
    }

    fun render() {
        val subTree = ReactRenderer.recursiveUnwrapping(func(), this)
        root.clearChilds()
        root.add(subTree)
        bind()
        gui.root.loadResources(gui.resources)
    }

    fun reRender(comp: IReactComponent<*, *>) {
        // fixme
        render()
        gui.editorPanel.let {
            it.recursiveUpdateSize(it.reactBase, Panel(), gui.windowHandler.window.getFrameBufferSize())
        }
    }
}

interface IComponentFactory<P, S, out C : IReactComponent<P, S>> {

    fun createDefaultProps(): P

    fun build(props: P): C

    operator fun invoke(props: P = createDefaultProps(), func: P.() -> Unit = {}): LeguiComponentBridge<P, S, C> {
        return LeguiComponentBridge(this, props)
    }
}

interface IScalable {

    fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2)
}
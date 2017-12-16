package com.cout970.modeler.gui.views

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.components.*
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RComponentRenderer.render
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorView : IView {

    override lateinit var gui: Gui

    override val base = panel {
        setTransparent()
        setBorderless()
    }

    override fun reBuild(newSize: IVector2) {

        base.position.set(0f, 0f)
        base.size = newSize.toJoml2f()

        val visible = VisibleElements(
                left = gui.state.showLeftPanel,
                bottom = gui.state.showBottomPanel,
                right = gui.state.showRightPanel
        )

        render(base, gui) {
            panel {
                size = newSize.toJoml2f()
                setTransparent()
                setBorderless()

                +TopButtonPanel { }
                +RightPanel {
                    RightPanel.Props(
                            modelAccessor = gui.modelAccessor,
                            selectedMaterial = { gui.state.selectedMaterial },
                            visibleElements = visible
                    )
                }
                +LeftPanel {
                    LeftPanel.Props(
                            access = gui.modelAccessor,
                            dispatcher = gui.dispatcher,
                            visibleElements = visible,
                            guiState = gui.state
                    )
                }
                +CenterPanel {
                    CenterPanel.Props(
                            visibleElements = visible,
                            canvasContainer = gui.canvasContainer
                    )
                }

                +BottomPanel {
                    BottomPanel.Props(
                            visibleElements = visible
                    )
                }

                gui.state.popup?.let {
                    when (it.name) {
                        "import" -> {
                            +ImportDialog { ImportDialog.Props(it) }
                        }
                        "export" -> {
                            +ExportDialog { ExportDialog.Props(it) }
                        }
                        "config" -> {
                            +ConfigMenu { ConfigMenu.Props(it) }
                        }
                    }
                }
            }
        }
    }
}

data class VisibleElements(val left: Boolean, val bottom: Boolean, val right: Boolean)
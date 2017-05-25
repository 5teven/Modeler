package com.cout970.modeler.to_redo.model.material

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import com.google.gson.annotations.Expose
import javax.swing.JOptionPane

class TexturedMaterial(@Expose override val name: String, val path: ResourcePath) : IMaterial {

    var texture: Texture? = null
    private var lastModified = -1L
    override val size: IVector2 get() = texture?.size ?: vec2Of(1)

    override fun loadTexture(resourceLoader: ResourceLoader) {
        try {
            texture = resourceLoader.getTexture(path.inputStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
            }
            lastModified = path.lastModifiedTime()
        } catch (e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error loading texture: Missing resource ($path)")
            texture = null
        }
    }

    override fun hasChanged(): Boolean {
        return lastModified != path.lastModifiedTime()
    }

    override fun bind() {
        texture?.bind() ?: MaterialNone.whiteTexture.bind()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TexturedMaterial) return false

        if (path != other.path) return false
        if (texture != other.texture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + (texture?.hashCode() ?: 0)
        return result
    }
}
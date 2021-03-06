package com.cout970.modeler.core.model.material

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec2Of
import java.util.*

class ColoredMaterial(override val name: String, val color: IVector3,
                      override val id: UUID = UUID.randomUUID()) : IMaterial {

    override val size: IVector2 = vec2Of(32)
    lateinit var whiteTexture: Texture
        private set

    override fun loadTexture(resourceLoader: ResourceLoader): Boolean {
        whiteTexture = resourceLoader.getTexture("assets/textures/white.png")
        return false
    }

    fun copy(name: String = this.name, color: IVector3 = this.color) =
            ColoredMaterial(name = name, color = color, id = id)

    override fun hasChanged(): Boolean = false

    override fun bind() {
        whiteTexture.bind()
    }
}
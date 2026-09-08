package net.bewis09.renderite.components

import net.bewis09.renderite.RenderiteElement
import net.bewis09.renderite.drawer.RenderiteDrawer
import net.bewis09.renderite.logic.Padding

class ImageElement<S: RenderiteDrawer<I, T, F>, T: Any, F, I: Any>(p: Props<ImageElement<S, T, F, I>>): RenderiteElement<S, ImageElement<S, T, F, I>, T, F, I>(p), Padding {
    lateinit var image: I
    var u = 0f
    var v = 0f
    var regionWidth: Int? = null
    var regionHeight: Int? = null
    var textureWidth: Int? = null
    var textureHeight: Int? = null
    override var padding: Int = 0
    override var verticalPadding: Int? = null
    override var horizontalPadding: Int? = null
    override var paddingLeft: Int? = null
    override var paddingTop: Int? = null
    override var paddingRight: Int? = null
    override var paddingBottom: Int? = null

    init { props() }

    override fun renderElement(screenDrawing: S, mouseX: Int, mouseY: Int) {
        val width = width - paddingLeft() - paddingRight()
        val height = height - paddingTop() - paddingBottom()

        screenDrawing.drawTextureRegion(image, x + paddingLeft(), y + paddingTop(), u, v, width, height, regionWidth ?: width, regionHeight ?: height, textureWidth ?: regionWidth ?: width, textureHeight ?: regionHeight ?: height)
    }
}
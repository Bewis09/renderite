package net.bewis09.renderite.components

import net.bewis09.renderite.Renderite
import net.bewis09.renderite.RenderiteElement
import net.bewis09.renderite.drawer.RenderiteDrawer
import net.bewis09.renderite.drawer.TextDrawing
import net.bewis09.renderite.logic.Color
import net.bewis09.renderite.logic.Padding
import net.bewis09.renderite.logic.TextAlign

class TextElement<S : RenderiteDrawer<I, T, F>, T : Any, F, I : Any>(p: Props<TextElement<S, T, F, I>>) : RenderiteElement<S, TextElement<S, T, F, I>, T, F, I>(p), Padding {
    var textProvider: () -> T = { text }
    var colorProvider: () -> Color = { color ?: Renderite.defaultTextColor() }
    lateinit var text: T
    var color: Color? = null
    var font: F? = null
    var widthResize = false
    var heightResize = false
    var textAlign = TextAlign.START
    var verticalAlign = TextAlign.CENTER
    override var padding: Int = 0
    override var verticalPadding: Int? = null
    override var horizontalPadding: Int? = null
    override var paddingLeft: Int? = null
    override var paddingTop: Int? = null
    override var paddingRight: Int? = null
    override var paddingBottom: Int? = null
    var wrap = false
    var lineHeight = 1f
    var shadow = false
    var fontSize: Float = Renderite.defaultFontSize

    init {
        props()
    }

    override fun renderLogic(screenDrawing: S, mouseX: Int, mouseY: Int) {
        if (widthResize) {
            width = screenDrawing.getTextWidth(textProvider(), getProperties()).toInt()
        }
    }

    override fun renderElement(screenDrawing: S, mouseX: Int, mouseY: Int) {
        val lines = if (wrap) screenDrawing.wrapText(textProvider(), width - paddingLeft() - paddingRight(), getProperties()) else listOf(textProvider())

        val y = when (verticalAlign) {
            TextAlign.START -> this.y.toFloat() + (paddingTop())
            TextAlign.CENTER -> centerY - lines.size / 2f * lineHeight * fontSize + (paddingTop()) / 2f - (paddingBottom()) / 2f
            TextAlign.END -> this.y2.toFloat() - lines.size * lineHeight * fontSize - (paddingBottom())
        }

        val x = when (textAlign) {
            TextAlign.START -> x + paddingLeft().toFloat()
            TextAlign.CENTER -> centerX + paddingLeft().toFloat() / 2 - paddingRight().toFloat() / 2
            TextAlign.END -> x2 - paddingRight().toFloat()
        }

        screenDrawing.drawWrappedText(lines, x, y, getProperties())

        if (heightResize)
            height = (lines.size * lineHeight * fontSize).toInt()
    }

    fun getProperties(): TextDrawing.Properties<F> = {
        font = this@TextElement.font
        lineHeight = this@TextElement.lineHeight
        color = colorProvider()
        shadow = this@TextElement.shadow
        textAlign = this@TextElement.textAlign
        fontSize = this@TextElement.fontSize
    }
}
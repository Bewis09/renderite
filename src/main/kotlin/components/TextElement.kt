package net.bewis09.renderite.components

import net.bewis09.renderite.Renderite
import net.bewis09.renderite.RenderiteElement
import net.bewis09.renderite.drawer.RenderiteDrawer
import net.bewis09.renderite.drawer.TextDrawing
import net.bewis09.renderite.logic.Color
import net.bewis09.renderite.logic.TextAlign

class TextElement<S : RenderiteDrawer<I, T, F>, T : Any, F, I : Any>(p: Props<TextElement<S, T, F, I>>) : RenderiteElement<S, TextElement<S, T, F, I>, T, F, I>(p) {
    var textProvider: () -> T = { text }
    var colorProvider: () -> Color = { color ?: Renderite.defaultTextColor() }
    lateinit var text: T
    var color: Color? = null
    var font: F? = null
    var widthResize = false
    var heightResize = false
    var textAlign = TextAlign.START
    var verticalAlign = TextAlign.CENTER
    var padding: Int = 0
    var verticalPadding: Int? = null
    var horizontalPadding: Int? = null
    var paddingLeft: Int? = null
    var paddingTop: Int? = null
    var paddingRight: Int? = null
    var paddingBottom: Int? = null
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
        val paddingTop = paddingTop ?: verticalPadding ?: padding
        val paddingBottom = paddingBottom ?: verticalPadding ?: padding
        val paddingLeft = paddingLeft ?: horizontalPadding ?: padding
        val paddingRight = paddingRight ?: horizontalPadding ?: padding

        val lines = if (wrap) screenDrawing.wrapText(textProvider(), width - paddingLeft - paddingRight, getProperties()) else listOf(textProvider())

        val y = when (verticalAlign) {
            TextAlign.START -> this.y.toFloat() + (paddingTop)
            TextAlign.CENTER -> centerY - lines.size / 2f * lineHeight * fontSize + (paddingTop) / 2f - (paddingBottom) / 2f
            TextAlign.END -> this.y2.toFloat() - lines.size * lineHeight * fontSize - (paddingBottom)
        }

        val x = when (textAlign) {
            TextAlign.START -> x + paddingLeft.toFloat()
            TextAlign.CENTER -> centerX + paddingLeft.toFloat() / 2 - paddingRight.toFloat() / 2
            TextAlign.END -> x2 - paddingRight.toFloat()
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
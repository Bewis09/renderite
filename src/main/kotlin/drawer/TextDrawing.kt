package net.bewis09.renderite.drawer

import net.bewis09.renderite.Renderite
import net.bewis09.renderite.logic.Color
import net.bewis09.renderite.logic.TextAlign

interface TextDrawing<I, T, F> : RectDrawing<I, T, F> {
    /**
     * Should draw the text in the given size and font from 0, 0 to the bottom-right
     */
    fun drawTextIntern(text: T, font: Properties<F>)

    fun drawText(text: T, x: Number, y: Number, font: Properties<F>) {
        translate(
            x.toFloat() - when (font().textAlign) {
                TextAlign.CENTER -> getTextWidth(text, font) / 2f
                TextAlign.END -> getTextWidth(text, font) * 1f
                else -> 0f
            }, y.toFloat()
        ) {
            drawTextIntern(text, font)
        }
    }

    fun drawWrappedText(lines: List<T>, x: Number, y: Number, font: Properties<F> = {}) {
        for (i in lines.indices) {
            drawText(lines[i], x, y.toFloat() + i * font().lineHeight * font().fontSize, font)
        }
    }

    fun drawWrappedText(text: T, x: Number, y: Number, maxWidth: Int, font: Properties<F> = {}): List<T> {
        return wrapText(text, maxWidth, font).also { drawWrappedText(it, x, y, font) }
    }

    fun wrapText(text: T, maxWidth: Int, font: Properties<F> = {}): List<T>

    fun getTextWidth(text: T, font: Properties<F> = {}): Float

    typealias Properties<F> = Font<F>.() -> Unit

    operator fun Properties<F>.invoke() = Font<F>().apply { this.this@invoke() }

    fun Properties<F>.getFont() = this().font ?: this@TextDrawing.overwrittenFont

    class Font<F> {
        var font: F? = null
        var fontSize: Float = Renderite.defaultFontSize
        var color: Color = Renderite.defaultTextColor()
        var shadow: Boolean = false
        var textAlign: TextAlign = TextAlign.START
        var lineHeight: Float = 1f
    }
}
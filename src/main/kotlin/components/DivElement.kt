package net.bewis09.renderite.components

import net.bewis09.renderite.Renderite
import net.bewis09.renderite.RenderiteElement
import net.bewis09.renderite.drawer.RenderiteDrawer
import net.bewis09.renderite.logic.Animator
import net.bewis09.renderite.logic.Direction
import net.bewis09.renderite.logic.FitType
import net.bewis09.renderite.logic.ItemAlign
import net.bewis09.renderite.logic.LineType
import kotlin.math.abs
import kotlin.math.roundToInt

open class DivElement<S: RenderiteDrawer<I, T, F>, T: Any, F, I: Any>(p: Props<DivElement<S, T, F, I>>) : RenderiteElement<S, DivElement<S, T, F, I>, T, F, I>(p) {
    var onInit: Init.(Int) -> Unit = {}
    var gap: Int = 0
    var minElementSize: Int = 100
    var lines = 1
    var lineType: LineType = LineType.DEFINITE
    var fitType: FitType = FitType.ENLARGE
    var itemAlign: ItemAlign = ItemAlign.STRETCH
    var elementsPerLine = 1
    var cacheChildren = false

    var direction: Direction = Direction.VERTICAL

    val scrollAnimation = Animator(Renderite.scrollAnimationTime, Animator.EASE_OUT, 0f)
    var innerSize = 0f

    private var lastDragX = null as Double?
    private var lastDragY = null as Double?

    private var hasScrollStartedVertical = false
    private var hasScrollStartedHorizontal = false

    var padding: Int = 0
    var verticalPadding: Int? = null
    var horizontalPadding: Int? = null
    var paddingLeft: Int? = null
    var paddingTop: Int? = null
    var paddingRight: Int? = null
    var paddingBottom: Int? = null

    var paddingOverflowVisible = true

    private var elementCache: List<RenderiteElement<S, *, T, F, I>>? = null

    init {
        props()
        if (Renderite.isStructureGenerating()) {
            width = 1000
            height = 1000
            resize()
        }
    }

    override fun renderLogic(screenDrawing: S, mouseX: Int, mouseY: Int) {
        updateSizeAndPosition()
    }

    override fun cleanup(screenDrawing: S, mouseX: Int, mouseY: Int) {
        if (!paddingOverflowVisible) screenDrawing.disableScissors()
    }

    override fun renderElement(screenDrawing: S, mouseX: Int, mouseY: Int) {
        if (!paddingOverflowVisible) {
            val paddingTop = paddingTop ?: verticalPadding ?: padding
            val paddingBottom = paddingBottom ?: verticalPadding ?: padding
            val paddingLeft = paddingLeft ?: horizontalPadding ?: padding
            val paddingRight = paddingRight ?: horizontalPadding ?: padding

            screenDrawing.enableScissors(x + paddingLeft, y + paddingTop, width - paddingRight - paddingLeft, height - paddingBottom - paddingTop)
        }
    }

    fun updateSizeAndPosition() {
        val elementSize = getElementSize()
        val startScroll = scrollAnimation.get().toInt()
        val linePosition = Array(getElementsInLine()) { startScroll.toFloat() + dirPaddingStart() }

        val fitHeight = (getOtherSpan() + gap) / elementsPerLine.toDouble() - gap

        for (it in ArrayList(renderables)) {
            val min = linePosition.minOrNull()?.toInt() ?: 0
            val lineIndex = linePosition.indexOf(min.toFloat())

            if (direction == Direction.VERTICAL) {
                val startX = x + conPaddingStart() + (lineIndex * (elementSize + gap)).roundToInt()

                when (itemAlign) {
                    ItemAlign.STRETCH -> {
                        it.updateWidth(elementSize.toInt())
                        it.updateX(startX)
                    }
                    ItemAlign.START -> {
                        it.updateX(startX)
                    }
                    ItemAlign.CENTER -> {
                        it.updateX(startX + elementSize.toInt() / 2 - it.width / 2)
                    }
                    ItemAlign.END -> {
                        it.updateX(startX + elementSize.toInt() - it.width)
                    }
                }

                it.updateY(y + min)

                if (fitType == FitType.FIT) {
                    it.updateHeight(fitHeight.toInt())
                    linePosition[lineIndex] += fitHeight.toFloat() + gap
                } else {
                    linePosition[lineIndex] += it.height + gap
                }
            } else {
                val startY = y + conPaddingStart() + (lineIndex * (elementSize + gap)).roundToInt()

                when (itemAlign) {
                    ItemAlign.STRETCH -> {
                        it.updateHeight(elementSize.toInt())
                        it.updateY(startY)
                    }
                    ItemAlign.START -> {
                        it.updateY(startY)
                    }
                    ItemAlign.CENTER -> {
                        it.updateY(startY + elementSize.toInt() / 2 - it.width / 2)
                    }
                    ItemAlign.END -> {
                        it.updateY(startY + elementSize.toInt() - it.width)
                    }
                }

                it.updateX(x + min)

                if (fitType == FitType.FIT) {
                    it.updateWidth(fitHeight.toInt())
                    linePosition[lineIndex] += fitHeight.toFloat() + gap
                } else {
                    linePosition[lineIndex] += it.width + gap
                }
            }
        }

        if (fitType == FitType.SCROLL) {
            innerSize = ((linePosition.maxOrNull() ?: 0f) - gap + dirPaddingEnd()) - startScroll
        } else if (fitType == FitType.ENLARGE) {
            if (direction == Direction.HORIZONTAL)
                width = (linePosition.maxOrNull() ?: 0f).toInt() - gap + dirPaddingEnd()
            else
                height = (linePosition.maxOrNull() ?: 0f).toInt() - gap + dirPaddingEnd()
        }
    }

    fun getTotalLinesSpan() = (if (direction == Direction.HORIZONTAL) height else width) - dirPaddingStart() - dirPaddingEnd()

    fun getOtherSpan() = (if (direction == Direction.HORIZONTAL) width else height) - conPaddingStart() - conPaddingEnd()

    fun getElementSize(): Double = (getTotalLinesSpan() + gap) / getElementsInLine().toDouble() - gap

    fun getElementsInLine(): Int = if (lineType == LineType.DEFINITE) lines else (getTotalLinesSpan() / (minElementSize + gap)).coerceAtLeast(1)

    override fun Init.init() {
        val cache = elementCache

        if (cacheChildren && cache != null) {
            addRenderables(cache)
        } else {
            onInit.invoke(this, getElementSize().toInt())
            elementCache = ArrayList(renderables)
        }

        updateSizeAndPosition()
    }

    fun <T> initForEach(collection: Collection<T>?, func: Init.(item: T) -> Unit) {
        onInit = { collection?.forEach { this.func(it) } }
    }

    fun <T, L> initForEach(map: Map<T, L>?, func: Init.(item: Map.Entry<T, L>) -> Unit) {
        onInit = { map?.forEach { this.func(it) } }
    }

    fun <T> initForEachIndexed(collection: Collection<T>?, func: Init.(i: Int, item: T) -> Unit) {
        onInit = { collection?.forEachIndexed { i, item -> this.func(i, item) } }
    }

    override fun onMouseScroll(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (fitType != FitType.SCROLL) return false
        scrollAnimation.set((scrollAnimation.getWithoutInterpolation() + (verticalAmount.toFloat() * 30f) + (horizontalAmount.toFloat() * 30f)).coerceIn(0f.coerceAtMost((if (direction == Direction.HORIZONTAL) width else height) - innerSize), 0f))
        return true
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, button: Int) {
        if (button != 0 || fitType != FitType.SCROLL) return

        lastDragX = null
        lastDragY = null

        hasScrollStartedVertical = false
        hasScrollStartedHorizontal = false

        return
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || fitType != FitType.SCROLL) return false

        lastDragX = null
        lastDragY = null

        hasScrollStartedVertical = false
        hasScrollStartedHorizontal = false

        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, startX: Double, startY: Double, button: Int): Boolean {
        if (button != 0 || fitType != FitType.SCROLL) return false

        if (abs(startX - mouseX) > 5 && direction == Direction.HORIZONTAL) hasScrollStartedHorizontal = true
        if (abs(startY - mouseY) > 5 && direction == Direction.VERTICAL) hasScrollStartedVertical = true

        val deltaX = if (hasScrollStartedHorizontal) (lastDragX ?: startX) - mouseX else 0.0
        val deltaY = if (hasScrollStartedVertical) (lastDragY ?: startY) - mouseY else 0.0

        if (direction == Direction.VERTICAL) {
            scrollAnimation.set((scrollAnimation.getWithoutInterpolation() - deltaY.toFloat()).coerceIn(0f.coerceAtMost((height - innerSize)), 0f))
        } else {
            scrollAnimation.set((scrollAnimation.getWithoutInterpolation() - deltaX.toFloat()).coerceIn(0f.coerceAtMost((width - innerSize)), 0f))
        }

        lastDragX = mouseX
        lastDragY = mouseY

        return true
    }

    fun dirPaddingStart() = if (direction == Direction.HORIZONTAL) paddingLeft ?: horizontalPadding ?: padding else paddingTop ?: verticalPadding ?: padding
    fun dirPaddingEnd() = if (direction == Direction.HORIZONTAL) paddingRight ?: horizontalPadding ?: padding else paddingBottom ?: verticalPadding ?: padding
    fun conPaddingStart() = if (direction == Direction.HORIZONTAL) paddingTop ?: verticalPadding ?: padding else paddingLeft ?: horizontalPadding ?: padding
    fun conPaddingEnd() = if (direction == Direction.HORIZONTAL) paddingBottom ?: verticalPadding ?: padding else paddingRight ?: horizontalPadding ?: padding
}
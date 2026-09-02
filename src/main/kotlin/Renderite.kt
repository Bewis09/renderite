package net.bewis09.renderite

import net.bewis09.renderite.logic.Color

object Renderite {
    var defaultFontSize = 9f
    var defaultTextColor = { Color.WHITE }
    var debugHighlightEnabled = { false }
    var debugBorderEnabled = { false }
    var debugUpdateHighlightEnabled = { false }
    var debugHighlightColor = { Color.YELLOW alpha 0.1f }
    var debugBorderColor = { Color.YELLOW alpha 0.2f }
    var debugUpdateHighlightColor = { Color.RED alpha 0.1f }
    var debugUpdateBorderColor = { Color.RED alpha 0.2f }
    var hoverTime = { 150L }
    var scrollAnimationTime = { 200L }

    /**
     * Whether the elements are only generating for data analysis purposes (e.g. translations or structure plans)
     */
    var isStructureGenerating = { false }
}
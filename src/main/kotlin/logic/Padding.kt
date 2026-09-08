package net.bewis09.renderite.logic

interface Padding {
    var padding: Int
    var horizontalPadding: Int?
    var verticalPadding: Int?
    var paddingLeft: Int?
    var paddingRight: Int?
    var paddingTop: Int?
    var paddingBottom: Int?

    fun paddingLeft() = paddingLeft ?: horizontalPadding ?: padding
    fun paddingRight() = paddingRight ?: horizontalPadding ?: padding
    fun paddingTop() = paddingTop ?: verticalPadding ?: padding
    fun paddingBottom() = paddingBottom ?: verticalPadding ?: padding
}
package com.rentify.util

sealed class ColorUtil(val color: String) {
    object ORANGE : ColorUtil("#EAB81C")
    object RED : ColorUtil("#FF0027")
    object PURPLE : ColorUtil("#793598")
    object BLUE : ColorUtil("#0001FE")
    object GREEN : ColorUtil("#30CA00")
    object DARKORANGE : ColorUtil("#FFA500")
}

//class GETCOLOR(val color: COLORS) {
//    enum class COLORS { ORANGE, RED, PURPLE, BLUE, GREEN, DARKORANGE }
//
//    fun get(): ColorUtil {
//        when (color) {
//            COLORS.ORANGE -> return ColorUtil.ORANGE
//            COLORS.RED -> return ColorUtil.RED
//            COLORS.PURPLE -> return ColorUtil.PURPLE
//            COLORS.BLUE -> return ColorUtil.BLUE
//            COLORS.GREEN -> return ColorUtil.GREEN
//            COLORS.DARKORANGE -> return ColorUtil.DARKORANGE
//        }
//    }
//}
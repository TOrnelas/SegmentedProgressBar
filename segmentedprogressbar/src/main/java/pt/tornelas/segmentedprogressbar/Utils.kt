package pt.tornelas.segmentedprogressbar

import android.content.Context
import android.util.TypedValue

fun Context.getThemeColor(attributeColor: Int): Int{
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attributeColor, typedValue, true)
    return typedValue.data
}
package pt.tornelas.segmentedprogressbar

import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue

fun Context.getThemeColor(attributeColor: Int): Int {
    val typedValue = TypedValue()
    this.theme.resolveAttribute(attributeColor, typedValue, true)
    return typedValue.data
}

fun SegmentedProgressBar.getDrawingComponents(
    segment: Segment,
    segmentIndex: Int
): Pair<MutableList<RectF>, MutableList<Paint>> {

    val rectangles = mutableListOf<RectF>()
    val paints = mutableListOf<Paint>()
    val segmentWidth = segmentWidth
    val startBound = segmentIndex * segmentWidth + ((segmentIndex) * margin)
    val endBound = startBound + segmentWidth
    val stroke = if (!strokeApplicable) 0f else this.segmentStrokeWidth.toFloat()

    val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = segmentBackgroundColor
    }

    val selectedBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = segmentSelectedBackgroundColor
    }

    val strokePaint = Paint().apply {
        color =
            if (segment.animationState == Segment.AnimationState.IDLE) segmentStrokeColor else segmentSelectedStrokeColor
        style = Paint.Style.STROKE
        strokeWidth = stroke
    }

    //Background component
    if (segment.animationState == Segment.AnimationState.ANIMATED) {
        rectangles.add(RectF(startBound + stroke, height - stroke, endBound - stroke, stroke))
        paints.add(selectedBackgroundPaint)
    } else {
        rectangles.add(RectF(startBound + stroke, height - stroke, endBound - stroke, stroke))
        paints.add(backgroundPaint)
    }

    //Progress component
    if (segment.animationState == Segment.AnimationState.ANIMATING) {
        rectangles.add(
            RectF(
                startBound + stroke,
                height - stroke,
                startBound + segment.progressPercentage * segmentWidth,
                stroke
            )
        )
        paints.add(selectedBackgroundPaint)
    }

    //Stroke component
    if (stroke > 0) {
        rectangles.add(RectF(startBound + stroke, height - stroke, endBound - stroke, stroke))
        paints.add(strokePaint)
    }

    return Pair(rectangles, paints)
}
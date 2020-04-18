package pt.tornelas.segmentedprogressbar

import android.graphics.Paint
import android.graphics.RectF

/**
 * Created by Tiago Ornelas on 18/04/2020.
 * Model that holds the segment state as well as drawing settings for each state
 */
class Segment(
        private var animationProgress: Int = 0,
        private val startBound: Float,
        private val endBound: Float,
        private val height: Float,
        private val stroke: Float
    ){

    var animationState: AnimationState = AnimationState.IDLE
        set(value) {
            animationProgress = when (value){
                AnimationState.ANIMATED -> 100
                AnimationState.IDLE -> 0
                else -> animationProgress
            }
            field = value
        }

    /**
     * Represents possible drawing states of the segment
     */
    enum class AnimationState{
        ANIMATED,
        ANIMATING,
        IDLE
    }

    private val progressPercentage: Float
        get() = animationProgress.toFloat() / 100

    private val rectangleWidth: Float
        get() = endBound - startBound

    fun getDrawingComponents(bg: Int, bgS: Int, s: Int, sS: Int): Pair<MutableList<RectF>, MutableList<Paint>>{

        val rectangles = mutableListOf<RectF>()
        val paints = mutableListOf<Paint>()

        val backgroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = bg
        }

        val selectedBackgroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = bgS
        }

        val strokePaint = Paint().apply {
            color = if(animationState == AnimationState.IDLE) s else sS
            style = Paint.Style.STROKE
            strokeWidth = stroke
        }

        //Background component
        if (animationState == AnimationState.ANIMATED){
            rectangles.add(RectF(startBound  + stroke, height - stroke, endBound - stroke, stroke))
            paints.add(selectedBackgroundPaint)
        }else{
            rectangles.add(RectF(startBound  + stroke, height - stroke, endBound - stroke, stroke))
            paints.add(backgroundPaint)
        }

        //Progress component
        if (animationState == AnimationState.ANIMATING){
            rectangles.add(
                RectF(startBound  + stroke, height - stroke, startBound + progressPercentage * rectangleWidth , stroke)
            )
            paints.add(selectedBackgroundPaint)
        }

        //Stroke component
        if (stroke > 0){
            rectangles.add(RectF(startBound  + stroke, height - stroke, endBound - stroke, stroke))
            paints.add(strokePaint)
        }

        return Pair(rectangles, paints)
    }

    fun progress(): Int{
        return animationProgress++
    }
}
package pt.tornelas.segmentedprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View


/**
 * Created by Tiago Ornelas on 18/04/2020.
 * Represents a segmented progress bar on which, the progress is set by segments
 */

class SegmentedProgressBar : View {

    private val TAG = SegmentedProgressBar::class.java.simpleName

    //Attributes
    private var totalSegments: Int = resources.getInteger(R.integer.default_segments_count)
    private var progress: Int = resources.getInteger(R.integer.default_progress)

    private var margin: Int = resources.getDimensionPixelSize(R.dimen.default_segment_margin)
    private var radius: Int = resources.getDimensionPixelSize(R.dimen.default_corner_radius)
    private var segmentStrokeWidth: Int = resources.getDimensionPixelSize(R.dimen.segmentStrokeWidth)

    private var segmentBackgroundColor: Int = Color.WHITE
    private var segmentSelectedBackgroundColor: Int =  context.getThemeColor(android.R.attr.colorAccent)

    private var segmentStrokeColor: Int = Color.BLACK
    private var segmentSelectedStrokeColor: Int =  Color.BLACK

    //Drawing objects
    private val itemPaint = Paint()
    private val itemRect = RectF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SegmentedProgressBar, 0, 0)

        totalSegments =
            typedArray.getInt(R.styleable.SegmentedProgressBar_totalSegments, totalSegments)
        progress =
            typedArray.getInt(R.styleable.SegmentedProgressBar_segmentProgress, progress)

        margin =
            typedArray.getDimensionPixelSize(R.styleable.SegmentedProgressBar_segmentMargins, margin)
        radius =
            typedArray.getDimensionPixelSize(R.styleable.SegmentedProgressBar_segmentCornerRadius, radius)
        segmentStrokeWidth =
            typedArray.getDimensionPixelSize(R.styleable.SegmentedProgressBar_segmentStrokeWidth, segmentStrokeWidth)

        segmentBackgroundColor =
            typedArray.getColor(R.styleable.SegmentedProgressBar_segmentBackgroundColor, segmentBackgroundColor)
        segmentSelectedBackgroundColor =
            typedArray.getColor(R.styleable.SegmentedProgressBar_segmentSelectedBackgroundColor, segmentSelectedBackgroundColor)

        segmentStrokeColor =
            typedArray.getColor(R.styleable.SegmentedProgressBar_segmentStrokeColor, segmentStrokeColor)
        segmentSelectedStrokeColor =
            typedArray.getColor(R.styleable.SegmentedProgressBar_segmentSelectedStrokeColor, segmentSelectedStrokeColor)

        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val strokeApplicable = segmentStrokeWidth * 4 <= measuredHeight

        if (!strokeApplicable){
            segmentStrokeWidth = 0
            Log.w(TAG, "Stroke not applicable. It's probably too thick")
        }

        val rectangleWidth: Float = (measuredWidth - margin * (totalSegments - 1)).toFloat() / totalSegments
        var segmentStartPoint = 0f

        for (i in 0 until totalSegments){

            val segmentEndPoint = segmentStartPoint + rectangleWidth
            itemRect.set(segmentStartPoint + segmentStrokeWidth, measuredHeight.toFloat() - segmentStrokeWidth, segmentEndPoint - segmentStrokeWidth,  segmentStrokeWidth.toFloat())

            //background
            itemPaint.color = if (progress - 1 >= i) segmentSelectedBackgroundColor else segmentBackgroundColor
            itemPaint.style = Paint.Style.FILL
            canvas?.drawRoundRect(itemRect, radius.toFloat(), radius.toFloat(), itemPaint)

            //stroke
            if (segmentStrokeWidth > 0){
                itemPaint.color = if (progress - 1 >= i) segmentSelectedStrokeColor else segmentStrokeColor
                itemPaint
                itemPaint.style = Paint.Style.STROKE
                itemPaint.strokeJoin = Paint.Join.ROUND
                itemPaint.strokeCap = Paint.Cap.ROUND
                itemPaint.strokeWidth = segmentStrokeWidth.toFloat()
                itemRect.set(segmentStartPoint + segmentStrokeWidth, measuredHeight.toFloat() - segmentStrokeWidth, segmentEndPoint - segmentStrokeWidth, segmentStrokeWidth.toFloat())
                canvas?.drawRoundRect(itemRect, radius.toFloat(), radius.toFloat(), itemPaint)
            }

            segmentStartPoint = segmentEndPoint + margin
        }
    }

    fun setProgress(progress: Int){
        this.progress = progress
        invalidate()
    }

    fun setProgressPercentage(progressPercentage: Int){
        this.progress = progressPercentage * totalSegments / 100
        invalidate()
    }
}
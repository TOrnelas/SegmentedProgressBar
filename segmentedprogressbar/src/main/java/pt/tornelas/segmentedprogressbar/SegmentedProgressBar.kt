package pt.tornelas.segmentedprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View

/**
 * Created by Tiago Ornelas on 18/04/2020.
 * Represents a segmented progress bar on which, the progress is set by segments
 * @see Segment
 * And the progress of each segment is animated based on a set speed
 */
class SegmentedProgressBar : View, Runnable {

    /**
     * Number of total segments to draw
     */
    var totalSegments: Int = resources.getInteger(R.integer.default_segments_count)
        private set

    private var margin: Int = resources.getDimensionPixelSize(R.dimen.default_segment_margin)
    private var radius: Int = resources.getDimensionPixelSize(R.dimen.default_corner_radius)
    private var segmentStrokeWidth: Int =
        resources.getDimensionPixelSize(R.dimen.default_segment_stroke_width)

    private var segmentBackgroundColor: Int = Color.WHITE
    private var segmentSelectedBackgroundColor: Int =
        context.getThemeColor(android.R.attr.colorAccent)

    private var segmentStrokeColor: Int = Color.BLACK
    private var segmentSelectedStrokeColor: Int = Color.BLACK

    private var timePerSegmentMs: Long =
        resources.getInteger(R.integer.default_time_per_segment_ms).toLong()


    private var segments = mutableListOf<Segment>()
    private val selectedSegment: Segment?
        get() = segments.firstOrNull { it.animationState == Segment.AnimationState.ANIMATING }


    private val animationHandler = Handler()
    private val animationUpdateTime: Long
        get() = timePerSegmentMs / 100

    //Drawing
    private val strokeApplicable: Boolean
        get() = segmentStrokeWidth * 4 <= measuredHeight

    private val segmentWidth: Float
        get() = (measuredWidth - margin * (totalSegments - 1)).toFloat() / totalSegments

    /**
     * Sets callbacks for progress bar state changes
     * @see SegmentedProgressBarListener
     */
    var listener: SegmentedProgressBarListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SegmentedProgressBar, 0, 0)

        totalSegments =
            typedArray.getInt(R.styleable.SegmentedProgressBar_totalSegments, totalSegments)

        margin =
            typedArray.getDimensionPixelSize(
                R.styleable.SegmentedProgressBar_segmentMargins,
                margin
            )
        radius =
            typedArray.getDimensionPixelSize(
                R.styleable.SegmentedProgressBar_segmentCornerRadius,
                radius
            )
        segmentStrokeWidth =
            typedArray.getDimensionPixelSize(
                R.styleable.SegmentedProgressBar_segmentStrokeWidth,
                segmentStrokeWidth
            )

        segmentBackgroundColor =
            typedArray.getColor(
                R.styleable.SegmentedProgressBar_segmentBackgroundColor,
                segmentBackgroundColor
            )
        segmentSelectedBackgroundColor =
            typedArray.getColor(
                R.styleable.SegmentedProgressBar_segmentSelectedBackgroundColor,
                segmentSelectedBackgroundColor
            )

        segmentStrokeColor =
            typedArray.getColor(
                R.styleable.SegmentedProgressBar_segmentStrokeColor,
                segmentStrokeColor
            )
        segmentSelectedStrokeColor =
            typedArray.getColor(
                R.styleable.SegmentedProgressBar_segmentSelectedStrokeColor,
                segmentSelectedStrokeColor
            )

        timePerSegmentMs =
            typedArray.getInt(
                R.styleable.SegmentedProgressBar_timePerSegment,
                timePerSegmentMs.toInt()
            ).toLong()

        typedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (segments.isEmpty()) {
            /** Loads segment modes based on the totalSegments **/
            setSegmentsCount()
        } else {
            segments.forEach {
                val drawingComponents = it.getDrawingComponents(
                    segmentBackgroundColor,
                    segmentSelectedBackgroundColor,
                    segmentStrokeColor,
                    segmentSelectedStrokeColor
                )
                drawingComponents.first.forEachIndexed { index, rectangle ->
                    canvas?.drawRoundRect(
                        rectangle,
                        radius.toFloat(),
                        radius.toFloat(),
                        drawingComponents.second[index]
                    )
                }
            }
        }
    }

    /**
     * Start/Resume progress animation
     */
    fun start() {
        pause()
        if (selectedSegment == null)
            next()
        else
            animationHandler.postDelayed(this, animationUpdateTime)
    }

    /**
     * Pauses the animation process
     */
    fun pause() {
        animationHandler.removeCallbacks(this)
    }

    /**
     * Resets the whole animation state and selected segments
     * !Doesn't restart it!
     * To restart, call the start() method
     */
    fun reset() {
        this.segments.map { it.animationState = Segment.AnimationState.IDLE }
        this.invalidate()
    }

    /**
     * Starts animation for the following segment
     */
    fun next() {
        loadSegment(offset = 1, userAction = true)
    }

    /**
     * Starts animation for the previous segment
     */
    fun previous() {
        loadSegment(offset = -1, userAction = true)
    }

    /**
     * Restarts animation for the current segment
     */
    fun restartSegment() {
        loadSegment(offset = 0, userAction = true)
    }

    /**
     * Skips a number of segments
     * @param offset number o segments fo skip
     */
    fun skip(offset: Int) {
        loadSegment(offset = offset, userAction = true)
    }

    /**
     * Init a SegmentedProgressBar with an amount of segments
     * @param count - the number of segments on the SegmentedProgressBar
     */
    fun setSegmentsCount(count: Int) {
        pause()
        this.totalSegments = count
        this.segments.clear()
        this.invalidate()
    }

    //Private methods
    private fun loadSegment(offset: Int, userAction: Boolean) {
        val currentSegmentIndex = this.segments.indexOf(this.selectedSegment)
        val nextSegmentIndex = currentSegmentIndex + offset
        if (userAction && nextSegmentIndex !in 0 until totalSegments) { //Index out of bounds, ignore operation
            return
        }

        segments.mapIndexed { index, segment ->
            if (offset > 0) {
                if (index < nextSegmentIndex) segment.animationState =
                    Segment.AnimationState.ANIMATED
            } else if (offset < 0) {
                if (index > nextSegmentIndex - 1) segment.animationState =
                    Segment.AnimationState.IDLE
            } else if (offset == 0) {
                if (index == nextSegmentIndex) segment.animationState = Segment.AnimationState.IDLE
            }
        }

        val nextSegment = this.segments.getOrNull(nextSegmentIndex)

        //Handle next segment transition/ending
        if (nextSegment != null) {
            pause()
            nextSegment.animationState = Segment.AnimationState.ANIMATING
            animationHandler.postDelayed(this, animationUpdateTime)
            this.listener?.onPage(currentSegmentIndex, segments.indexOf(this.selectedSegment))
        } else {
            animationHandler.removeCallbacks(this)
            this.listener?.onFinished()
        }
    }

    private fun setSegmentsCount() {
        var segmentStartPoint = 0f
        for (i in 0 until totalSegments) {
            val segmentEndPoint = segmentStartPoint + segmentWidth
            segments.add(
                Segment(
                    startBound = segmentStartPoint,
                    endBound = segmentEndPoint,
                    height = measuredHeight.toFloat(),
                    stroke = if (strokeApplicable) segmentStrokeWidth.toFloat() else 0f
                )
            )
            segmentStartPoint = segmentEndPoint + margin
        }

        this.invalidate()

        reset()
    }

    override fun run() {
        val progress = this.selectedSegment?.progress()
        if (progress ?: 0 >= 100) {
            loadSegment(offset = 1, userAction = false)
        } else {
            this.invalidate()
            animationHandler.postDelayed(this, animationUpdateTime)
        }
    }
}
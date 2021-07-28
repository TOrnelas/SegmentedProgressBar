package pt.tornelas.segmentedprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

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
    var segmentCount: Int = resources.getInteger(R.integer.default_segments_count)
        set(value) {
            field = value
            this.initSegments()
        }

    var margin: Int = resources.getDimensionPixelSize(R.dimen.default_segment_margin)
        private set
    var radius: Int = resources.getDimensionPixelSize(R.dimen.default_corner_radius)
        private set
    var segmentStrokeWidth: Int =
        resources.getDimensionPixelSize(R.dimen.default_segment_stroke_width)
        private set

    var segmentBackgroundColor: Int = Color.WHITE
        private set
    var segmentSelectedBackgroundColor: Int =
        context.getThemeColor(android.R.attr.colorAccent)
        private set
    var segmentStrokeColor: Int = Color.BLACK
        private set
    var segmentSelectedStrokeColor: Int = Color.BLACK
        private set

    var timePerSegmentMs: Long =
        resources.getInteger(R.integer.default_time_per_segment_ms).toLong()
        private set

    private var segments = mutableListOf<Segment>()
    private val selectedSegment: Segment?
        get() = segments.firstOrNull { it.animationState == Segment.AnimationState.ANIMATING }
    private val selectedSegmentIndex: Int
        get() = segments.indexOf(this.selectedSegment)

    private val animationHandler = Handler()
    private val animationUpdateTime: Long
        get() = timePerSegmentMs / 100

    //Drawing
    val strokeApplicable: Boolean
        get() = segmentStrokeWidth * 4 <= measuredHeight

    val segmentWidth: Float
        get() = (measuredWidth - margin * (segmentCount - 1)).toFloat() / segmentCount

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { _, event ->
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> pause()
            MotionEvent.ACTION_UP -> start()
        }
        false
    }

    // VIEWPAGER
    private val onPageChangeLister = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            setPosition(position)
        }
    }

    var viewPager: ViewPager? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            field = value
            if (value == null) {
                viewPager?.removeOnPageChangeListener(onPageChangeLister)
                viewPager?.setOnTouchListener(null)
            } else {
                viewPager?.addOnPageChangeListener(onPageChangeLister)
                viewPager?.setOnTouchListener(onTouchListener)
            }
        }

    // VIEWPAGER2
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            setPosition(position)
        }
    }

    var viewPager2: ViewPager2? = null
        @SuppressLint("ClickableViewAccessibility")
        set(value) {
            field = value
            if (value == null) {
                viewPager2?.unregisterOnPageChangeCallback(onPageChangeCallback)
                viewPager2?.getChildAt(0)?.setOnTouchListener(null)
            } else {
                viewPager2?.registerOnPageChangeCallback(onPageChangeCallback)
                viewPager2?.getChildAt(0)?.setOnTouchListener(onTouchListener)
            }
        }

    /**
     * Sets callbacks for progress bar state changes
     * @see SegmentedProgressBarListener
     */
    var listener: SegmentedProgressBarListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SegmentedProgressBar, 0, 0)

        segmentCount =
            typedArray.getInt(R.styleable.SegmentedProgressBar_totalSegments, segmentCount)

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

        segments.forEachIndexed { index, segment ->
            val drawingComponents = getDrawingComponents(segment, index)
            drawingComponents.first.forEachIndexed { drawingIndex, rectangle ->
                canvas?.drawRoundRect(
                    rectangle,
                    radius.toFloat(),
                    radius.toFloat(),
                    drawingComponents.second[drawingIndex]
                )
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
     * Sets current segment to the
     * @param position index
     */
    fun setPosition(position: Int) {
        loadSegment(offset = position - this.selectedSegmentIndex, userAction = true)
    }

    //Private methods
    private fun loadSegment(offset: Int, userAction: Boolean) {
        val oldSegmentIndex = this.segments.indexOf(this.selectedSegment)

        val nextSegmentIndex = oldSegmentIndex + offset

        //Index out of bounds, ignore operation
        if (userAction && nextSegmentIndex !in 0 until segmentCount) {
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
            this.listener?.onPage(oldSegmentIndex, this.selectedSegmentIndex)
            viewPager?.let {
                it.currentItem = this.selectedSegmentIndex
            } ?: viewPager2?.let {
                it.currentItem = this.selectedSegmentIndex
            }
        } else {
            animationHandler.removeCallbacks(this)
            this.listener?.onFinished()
        }
    }

    private fun initSegments() {
        this.segments.clear()
        segments.addAll(List(segmentCount) { Segment() })
        this.invalidate()
        reset()
    }

    override fun run() {
        if (this.selectedSegment?.progress() ?: 0 >= 100) {
            loadSegment(offset = 1, userAction = false)
        } else {
            this.invalidate()
            animationHandler.postDelayed(this, animationUpdateTime)
        }
    }
}
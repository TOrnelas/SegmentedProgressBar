package pt.tornelas.segmentedprogressbar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val pages = listOf(
        "Page 1",
        "Page 2",
        "Page 3",
        "Page 4",
        "Page 5",
        "Page 6",
        "Page 7"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPauseAnimation.setOnClickListener { segmentedProgressBar.pause() }
        btnResetAnimation.setOnClickListener { segmentedProgressBar.reset() }
        btnStartAnimation.setOnClickListener { segmentedProgressBar.start() }
        btnNext.setOnClickListener { segmentedProgressBar.next() }
        btnPrevious.setOnClickListener { segmentedProgressBar.previous() }
        btnRestartSegment.setOnClickListener { segmentedProgressBar.restartSegment() }
        btnIncrease.setOnClickListener { segmentedProgressBar.setSegmentsCount(20) }

        segmentedProgressBar.setSegmentsCount(10)
        segmentedProgressBar.start()

        segmentedProgressBar.listener = object : SegmentedProgressBarListener{
            override fun onPage(oldPageIndex: Int, newPageIndex: Int) {
                Log.i("MAINACTIVITY", "onOldPage $oldPageIndex onNEwPage $newPageIndex")
            }

            override fun onFinished() {
                Log.i("MAINACTIVITY", "onFinished")
            }

        }
    }
}

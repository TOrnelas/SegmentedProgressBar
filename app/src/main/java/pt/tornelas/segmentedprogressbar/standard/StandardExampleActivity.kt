package pt.tornelas.segmentedprogressbar.standard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_standard_example.*
import kotlinx.android.synthetic.main.layout_pager.*
import pt.tornelas.segmentedprogressbar.R
import pt.tornelas.segmentedprogressbar.SegmentedProgressBar
import pt.tornelas.segmentedprogressbar.SegmentedProgressBarListener
import pt.tornelas.segmentedprogressbar.dataSource
import pt.tornelas.segmentedprogressbar.pager.PagerAdapter

class StandardExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_example)

        val items = dataSource()

        viewPager.adapter = PagerAdapter(supportFragmentManager, items)
        spb.viewPager = viewPager

        spb.segmentCount = items.size
        spb.listener = object : SegmentedProgressBarListener {
            override fun onPage(oldPageIndex: Int, newPageIndex: Int) {
                // New page started animating
            }

            override fun onFinished() {
                finish()
            }
        }

        val spb = findViewById<SegmentedProgressBar>(R.id.spb)
//        spb.start()

        btnNext.setOnClickListener { spb.next() }
        btnPrevious.setOnClickListener { spb.previous() }
    }
}

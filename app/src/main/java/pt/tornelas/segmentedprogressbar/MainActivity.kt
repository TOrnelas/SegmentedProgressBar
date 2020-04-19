package pt.tornelas.segmentedprogressbar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pt.tornelas.segmentedprogressbar.standard.StandardExampleActivity

class MainActivity : AppCompatActivity() {

    companion object{
        const val LAYOUT_TYPE_STANDARD = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStandardLayout.setOnClickListener { loadExample(LAYOUT_TYPE_STANDARD) }
    }

    private fun loadExample(type: Int){
        startActivity(
            Intent(
                this,
                when (type){
                    LAYOUT_TYPE_STANDARD -> StandardExampleActivity::class.java
                    else -> throw RuntimeException("Unrecognized activity")
                }
            )
        )
    }
}

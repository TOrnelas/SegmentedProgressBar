package pt.tornelas.segmentedprogressbar.pager

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_page.*
import pt.tornelas.segmentedprogressbar.R

private const val PAGE_INDEX = "PAGE_INDEX"

class PageFragment : Fragment(), RequestListener<Drawable> {

    companion object {
        @JvmStatic
        fun newInstance(pageIndex: Int) =
            PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_INDEX, pageIndex)
                }
            }
    }

    private var pageIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageIndex = it.getInt(PAGE_INDEX)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_page, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE
        val aleatoryIndex = (1..50).random()

        Glide.with(this)
            .load("https://picsum.photos/id/${pageIndex + aleatoryIndex}/400/600")
            .listener(this)
            .into(image)
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        return true
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        progressBar.visibility = View.GONE
        return false
    }
}

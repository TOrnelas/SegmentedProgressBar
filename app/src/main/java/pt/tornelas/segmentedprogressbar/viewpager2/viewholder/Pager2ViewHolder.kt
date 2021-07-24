package pt.tornelas.segmentedprogressbar.viewpager2.viewholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_page.view.*
import pt.tornelas.segmentedprogressbar.R

class Pager2ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun newInstance(parent: ViewGroup): Pager2ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.fragment_page, parent, false)
            return Pager2ViewHolder(view)
        }
    }

    fun bind(pageIndex: Int) {
        val aleatoryIndex = (1..50).random()
        Glide.with(itemView.context)
            .load("https://picsum.photos/id/${pageIndex + aleatoryIndex}/400/600")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean) = true

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    itemView.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(itemView.image)
    }
}
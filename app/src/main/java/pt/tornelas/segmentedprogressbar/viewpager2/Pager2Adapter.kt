package pt.tornelas.segmentedprogressbar.viewpager2

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.tornelas.segmentedprogressbar.viewpager2.viewholder.Pager2ViewHolder

class Pager2Adapter constructor(private val items: List<Int>) :
    RecyclerView.Adapter<Pager2ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Pager2ViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = items.size
}
package com.example.wds.fragments.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wds.R
import com.example.wds.entry.Entry
import kotlinx.android.synthetic.main.log_item.view.*

class LogListAdapter : RecyclerView.Adapter<LogListAdapter.LogViewHolder>() {
    private var logList = emptyList<Entry>()
    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.log_pic
        private val textView1: TextView = itemView.tv_animal
        private val textView2: TextView = itemView.tv_timestamp

        fun bind(entry: Entry) {
            textView1.text = entry.textAnimal
            textView2.text = entry.textTime

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_image_400)
                .error(R.drawable.ic_image_400)
                .fitCenter()

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(entry.imgSrc)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent,false)
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val current = logList[position]
        holder.bind(current)
    }

    override fun getItemCount() = logList.size

    fun setEntries(entries : List<Entry>) {
//        println("DEBUG LogListAdapter->setEntries()")
        logList = entries
        notifyDataSetChanged()
    }
}
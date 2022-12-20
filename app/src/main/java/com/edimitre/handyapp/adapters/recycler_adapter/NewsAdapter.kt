package com.edimitre.handyapp.adapters.recycler_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.model.Note


class NewsAdapter : PagingDataAdapter<News, NewsAdapter.NewsViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)

    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<News>() {

        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }

    }

    class NewsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        private val newsTitle: TextView = itemView.findViewById(R.id.news_title)
        private val newsParagraph: TextView = itemView.findViewById(R.id.news_paragraph)
        private val newsSource: TextView = itemView.findViewById(R.id.news_source)
        private val newsLink: TextView = itemView.findViewById(R.id.news_link)


        fun bind(news: News) {
            newsTitle.text = news.title
            newsParagraph.text = news.paragraph
            newsSource.text = news.source
            newsLink.text = news.link
        }
    }

    fun getNewsByPos(pos: Int): News? {
        return getItem(pos)
    }
}
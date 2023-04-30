package com.edimitre.handyapp.adapters.recycler_adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.News


class NewsAdapter(private val onNewsClickListener: OnNewsClickListener) :
    PagingDataAdapter<News, NewsAdapter.NewsViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)

    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, onNewsClickListener)

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


        private val newsTitle: TextView = itemView.findViewById(R.id.file_name)
        private val newsParagraph: TextView = itemView.findViewById(R.id.news_paragraph)
        private val newsSource: TextView = itemView.findViewById(R.id.news_source)
        private val newsLink: TextView = itemView.findViewById(R.id.news_link)
        private val newsLikeBtn: CardView = itemView.findViewById(R.id.button_like)
        private val likeImage: ImageView = itemView.findViewById(R.id.like_img)
        private val newsShareBtn: CardView = itemView.findViewById(R.id.button_share_news)
        fun bind(news: News, newsLikeClickListener: OnNewsClickListener) {
            newsTitle.text = news.title
            newsParagraph.text = news.paragraph
            newsSource.text = "source ${news.source}"
            newsLink.text = "link ${news.link}"

            if (news.liked) {
                likeImage.setImageResource(R.drawable.ic_check_circle)
            }

            newsLikeBtn.setOnClickListener {
                newsLikeClickListener.onLikeClicked(news)
            }
            newsShareBtn.setOnClickListener {
                newsLikeClickListener.onShareClicked(news)
            }


        }
    }

    fun getNewsByPos(pos: Int): News? {
        return getItem(pos)
    }

    interface OnNewsClickListener {
        fun onLikeClicked(news: News)
        fun onShareClicked(news: News)
    }


}
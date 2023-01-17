package com.example.newsapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.newsapp.INewsRecycler
import com.example.newsapp.R
import com.example.newsapp.databinding.NewItemBinding
import com.example.newsapp.network.models.Article
import com.example.newsapp.ui.models.RecyclerWrapper

class NewsRecyclerAdapter (private val callback: INewsRecycler? = null): RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>() {

    private var data = emptyList<RecyclerWrapper<Article>>()

    fun setData(data: List<RecyclerWrapper<Article>>){
        this.data = data
        notifyDataSetChanged()
    }
    fun getData() = data

    inner class NewsViewHolder(private val binding: NewItemBinding): ViewHolder(binding.root){

        private fun onFavoriteClickListener(){
            val position = adapterPosition
            if(RecyclerView.NO_POSITION != position){
                val item = data[adapterPosition]
                callback?.onFavoriteListener(item.data)
            }
        }

        init {
            binding.imageFavorite.setOnClickListener{onFavoriteClickListener()}
            binding.labelFavorite.setOnClickListener{onFavoriteClickListener()}
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(RecyclerView.NO_POSITION != position){
                    val item = data[adapterPosition]
                    callback?.onClickListener(item.data)
                }
            }
        }

        fun bind(item: RecyclerWrapper<Article>){
            binding.apply {
                textTitle.text = item.data.title
                textDescription.text = item.data.description
                textSource.text = item.data.source.name
                if(item.isFavorite){
                    imageFavorite.setImageResource(R.drawable.ic_favorite)
                    labelFavorite.text = root.context.getString(R.string.added_favorite)
                }else{
                    imageFavorite.setImageResource(R.drawable.ic_unfavorite)
                    labelFavorite.text = root.context.getString(R.string.add_favorite)
                }
                imageCover.load(item.data.urlToImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(NewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
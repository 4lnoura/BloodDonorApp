package com.blood_donor.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blood_donor.R;
import com.blood_donor.models.News;
import com.bumptech.glide.Glide;

import java.util.ArrayList;



public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private ArrayList<News> news = new ArrayList<>();

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setNews(news.get(position));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public void setNews(ArrayList<News> news) {
        this.news = new ArrayList<>(news);
        this.notifyDataSetChanged();
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private News news;
        private TextView title, description;

        public NewsViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        public void setNews(News news) {
            this.news = news;
            Glide.with(itemView.getContext())
                    .load(news.getImage().isEmpty() ? itemView.getContext().getResources().getDrawable(R.drawable.placeholder) :
                            news.getImage())
                    .into(imageView);
            description.setText(news.getDescription());
            title.setText(news.getTitle());
        }

    }
}


package com.artur.simpleTheGuardianApp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.artur.simpleTheGuardianApp.Connections.ServerConnections;
import com.artur.simpleTheGuardianApp.Holders.ViewHolder;
import com.artur.simpleTheGuardianApp.R;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;

import java.util.ArrayList;

import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.TYPE_GET_ARTICLES;
import static com.artur.simpleTheGuardianApp.Utils.Utils.convertRawDateToYMDFormat;

/**
 * Created by artur on 02-Mar-19.
 */

public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<ArticleVO> articleVOS;

    public ListAdapter(ArrayList<ArticleVO> articleVOS) {
        this.articleVOS = articleVOS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImageLoader.getInstance().displayImage(articleVOS.get(position).getImageURL(), holder.imageView);

        holder.title.setText(articleVOS.get(position).getTitle());
        holder.category.setText(articleVOS.get(position).getCategory());
        holder.date.setText(convertRawDateToYMDFormat(articleVOS.get(position).getPublicationDate()));
        if (position == (getItemCount() - 1)) {
            ServerConnections.getServerConnectionsInstance().sendRequest(TYPE_GET_ARTICLES);
        }
    }

    @Override
    public int getItemCount() {
        return articleVOS.size();
    }

    public void addArticles(ArrayList<ArticleVO> articleVOS) {
        this.articleVOS.addAll(articleVOS);
        notifyItemInserted(this.articleVOS.size());
    }

}

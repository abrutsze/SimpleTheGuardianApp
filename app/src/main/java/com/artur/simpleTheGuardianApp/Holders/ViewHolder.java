package com.artur.simpleTheGuardianApp.Holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.artur.simpleTheGuardianApp.Activites.ArticlePageActivity;
import com.artur.simpleTheGuardianApp.R;

import static com.artur.simpleTheGuardianApp.Activites.ArticlePageActivity.INTENT_KEY_ARTICLE_INDEX;

/**
 * Created by artur on 02-Mar-19.
 */

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;
    public TextView title, category, date;

    public ViewHolder(final View itemView) {
        super(itemView);
        itemView.findViewById(R.id.item).setOnClickListener(this);
        imageView = itemView.findViewById(R.id.image);
        title = itemView.findViewById(R.id.title);
        category = itemView.findViewById(R.id.category);
        date = itemView.findViewById(R.id.date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item:
                Intent intent = new Intent(v.getContext(), ArticlePageActivity.class);
                intent.putExtra(INTENT_KEY_ARTICLE_INDEX, getLayoutPosition());
                v.getContext().startActivity(intent);
                break;
            default:
                break;
        }

    }

}

package com.artur.simpleTheGuardianApp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.artur.simpleTheGuardianApp.R;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;

import java.util.ArrayList;

/**
 * Created by artur on 02-Mar-19.
 */

public class SearchAdapter extends BaseAdapter implements Filterable {
    private SearchFilter searchFilter;
    private ArrayList<ArticleVO> allArticlesVOS;
    private ArrayList<ArticleVO> filteredArticlesVOS;

    public SearchAdapter(ArrayList<ArticleVO> articleVOS) {
        this.allArticlesVOS = articleVOS;
        this.filteredArticlesVOS = articleVOS;
        getFilter();
    }

    @Override
    public int getCount() {
        return filteredArticlesVOS.size();
    }

    @Override
    public ArticleVO getItem(int i) {
        return filteredArticlesVOS.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_search, parent, false);
            holder = new ViewHolder();
            holder.name = view.findViewById(R.id.tv);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(getItem(position).getTitle());

        return view;
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null) {
            searchFilter = new SearchFilter();
        }

        return searchFilter;
    }

    static class ViewHolder {
        TextView name;
    }

    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<ArticleVO> tempList = new ArrayList<>();

                for (ArticleVO articleVO : allArticlesVOS) {
                    if (articleVO.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(articleVO);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = allArticlesVOS.size();
                filterResults.values = allArticlesVOS;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredArticlesVOS = (ArrayList<ArticleVO>) results.values;
            notifyDataSetChanged();
        }
    }

}
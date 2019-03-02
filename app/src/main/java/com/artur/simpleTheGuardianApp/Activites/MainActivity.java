package com.artur.simpleTheGuardianApp.Activites;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.artur.simpleTheGuardianApp.Adapters.ListAdapter;
import com.artur.simpleTheGuardianApp.Adapters.SearchAdapter;
import com.artur.simpleTheGuardianApp.Controllers.ArticlesController;
import com.artur.simpleTheGuardianApp.Enums.ViewType;
import com.artur.simpleTheGuardianApp.R;
import com.artur.simpleTheGuardianApp.SpacesItemDecoration;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;

import java.util.ArrayList;

import static com.artur.simpleTheGuardianApp.Activites.ArticlePageActivity.INTENT_KEY_ARTICLE_INDEX;
import static com.artur.simpleTheGuardianApp.Controllers.ArticlesController.getArticlesControllerInstance;
import static com.artur.simpleTheGuardianApp.Controllers.ArticlesController.initArticlesControllerInstance;
import static com.artur.simpleTheGuardianApp.SharedPreferences.SharedPreferenceManager.getSharedPreferenceManager;

/**
 * Created by artur on 02-Mar-19.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener, ArticlesController.Articles {

    private RecyclerView allArticlesRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ListAdapter listAdapter;
    private ArrayList<ArticleVO> articleVOS = new ArrayList<>();

    private ListView lv;
    private SearchAdapter searchAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allArticlesRecyclerView = findViewById(R.id.all_articles);
        lv = findViewById(R.id.search_list);

        listAdapter = new ListAdapter(articleVOS);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(getSharedPreferenceManager().getViewType(), StaggeredGridLayoutManager.VERTICAL);

        allArticlesRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        allArticlesRecyclerView.setAdapter(listAdapter);

        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        allArticlesRecyclerView.addItemDecoration(decoration);

        initArticlesControllerInstance(this);
        getArticlesControllerInstance().registerForArticles(this);
        getArticlesControllerInstance().getAllArticles();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnSearchClickListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                lv.setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_view_type:
                if (getSharedPreferenceManager().getViewType() == ViewType.LIST.type()) {
                    getSharedPreferenceManager().setViewType(ViewType.PINTEREST);
                } else {
                    getSharedPreferenceManager().setViewType(ViewType.LIST);
                }
                staggeredGridLayoutManager.setSpanCount(getSharedPreferenceManager().getViewType());
                allArticlesRecyclerView.setLayoutManager(staggeredGridLayoutManager);

                return true;
            case R.id.search:
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            lv.setVisibility(View.VISIBLE);
            lv.bringToFront();
            searchAdapter.getFilter().filter(newText);
        }
        return true;
    }


    private void initTextsList() {
        searchAdapter = new SearchAdapter(articleVOS);

        lv.setAdapter(searchAdapter);
        lv.setTextFilterEnabled(false);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= articleVOS.size()) {
                    handelListItemClick(searchAdapter.getItem(position));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (lv.getVisibility() == View.VISIBLE) {
            lv.setVisibility(View.GONE);
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    private void handelListItemClick(ArticleVO selectedArticleVO) {
        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
            lv.setVisibility(View.GONE);
            searchView.onActionViewCollapsed();
        }

        for (int i = 0; i < articleVOS.size(); i++) {
            if (articleVOS.get(i).getTitle().toLowerCase().contains(selectedArticleVO.getTitle().toLowerCase())) {
                Intent intent = new Intent(this, ArticlePageActivity.class);
                intent.putExtra(INTENT_KEY_ARTICLE_INDEX, i);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                initTextsList();
                break;
            default:
                break;
        }
    }

    @Override
    public void onArticlesReady(final ArrayList<ArticleVO> articleVOS) {
        this.articleVOS = articleVOS;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.addArticles(articleVOS);
            }
        });
    }
}

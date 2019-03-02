package com.artur.simpleTheGuardianApp.Activites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.artur.simpleTheGuardianApp.DetectSwipeGestureListener;
import com.artur.simpleTheGuardianApp.R;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;

import static com.artur.simpleTheGuardianApp.Controllers.ArticlesController.getArticlesControllerInstance;

/**
 * Created by artur on 02-Mar-19.
 */

public class ArticlePageActivity extends AppCompatActivity implements DetectSwipeGestureListener.Swipe {

    public static final String INTENT_KEY_ARTICLE_INDEX = "com.artur.simpleTheGuardianApp.Activites.ArticlePageActivity.INTENT_KEY_ARTICLE_INDEX";

    private int articleIndex;
    private TextView title, content;
    private ImageView image;

    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_page);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        image = findViewById(R.id.image);


        if (getIntent().hasExtra(INTENT_KEY_ARTICLE_INDEX)) {
            articleIndex = getIntent().getIntExtra(INTENT_KEY_ARTICLE_INDEX, 0);

            ArticleVO articleVO = getArticlesControllerInstance().getArticleByIndex(articleIndex);

            ImageLoader.getInstance().displayImage(articleVO.getImageURL(), image);

            title.setText(articleVO.getTitle());
            content.setText(articleVO.getContent());

        }

        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        gestureListener.registerForSwipeDetection(this);

        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    @Override
    public void onSwipeDetected(int direction) {
        ArticleVO articleVO = null;
        int tempIndex = articleIndex;
        switch (direction) {
            case DetectSwipeGestureListener.SWIPE_DIRECTION_LEFT:
                tempIndex += 1;
                articleVO = getArticlesControllerInstance().getArticleByIndex(tempIndex);
                break;
            case DetectSwipeGestureListener.SWIPE_DIRECTION_RIGHT:
                tempIndex -= 1;
                articleVO = getArticlesControllerInstance().getArticleByIndex(tempIndex);
                break;
            default:
                break;
        }
        if (articleVO != null) {
            articleIndex = tempIndex;
            ImageLoader.getInstance().displayImage(articleVO.getImageURL(), image);
            title.setText(articleVO.getTitle());
            content.setText(articleVO.getContent());
        }
    }

}

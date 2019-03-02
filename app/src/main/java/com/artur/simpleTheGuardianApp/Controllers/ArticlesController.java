package com.artur.simpleTheGuardianApp.Controllers;

import android.app.Activity;
import android.widget.Toast;

import com.artur.simpleTheGuardianApp.Connections.ServerConnections;
import com.artur.simpleTheGuardianApp.R;
import com.artur.simpleTheGuardianApp.Utils.CheckInternet;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;

import java.util.ArrayList;

import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.ERROR_NO_ROUTE_TO_HOST_EXCEPTION;
import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.ERROR_SERVER_TIMEOUT;
import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.ERROR_UNKNOWN;
import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.TYPE_GET_ARTICLES;
import static com.artur.simpleTheGuardianApp.Connections.ServerConnections.WARNING_LAST_ITEM_REACHED;
import static com.artur.simpleTheGuardianApp.Utils.CheckInternet.INTERNET_AVAILABLE;
import static com.artur.simpleTheGuardianApp.Utils.LogsClass.getLogsClassInstance;

/**
 * Created by artur on 02-Mar-19.
 */

public class ArticlesController implements ServerConnections.ServerResponse, CheckInternet.InternetState {

    private static ArticlesController articlesController;
    private ArrayList<ArticleVO> articleVOS = new ArrayList<>();
    private Activity activity;
    private Articles articles;

    private ArticlesController(Activity activity) {
        this.activity = activity;
    }

    public static void initArticlesControllerInstance(Activity context) {
        if (articlesController == null) {
            articlesController = new ArticlesController(context);
        }
    }

    public static ArticlesController getArticlesControllerInstance() {
        return articlesController;
    }

    public void getAllArticles() {
        if (articleVOS.isEmpty()) {
            CheckInternet checkInternet = new CheckInternet(activity);
            checkInternet.registerForInternetState(this);
            if (checkInternet.isInternetAvailable()) {
                ServerConnections.getServerConnectionsInstance().registerForServerResponse(this);
                ServerConnections.getServerConnectionsInstance().sendRequest(TYPE_GET_ARTICLES);
            }
        } else if (articles != null) {
            articles.onArticlesReady(articleVOS);
        }
    }

    public ArticleVO getArticleByIndex(int position) {
        if (position < articleVOS.size() && position >= 0) {
            return articleVOS.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void serverResponseStatus(int type, int responseCode, final Object responseMessage, String errorBody) {

        getLogsClassInstance().d("type= " + type);

        switch (responseCode) {
            case 200:
                switch (type) {
                    case TYPE_GET_ARTICLES:
                        articleVOS = (ArrayList<ArticleVO>) responseMessage;
                        if (articles != null) {
                            articles.onArticlesReady(articleVOS);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case ERROR_SERVER_TIMEOUT:
            case ERROR_NO_ROUTE_TO_HOST_EXCEPTION:
            case ERROR_UNKNOWN:
                Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                break;
            case WARNING_LAST_ITEM_REACHED:
                break;
            default:
                break;
        }
    }

    @Override
    public void internetStateChanged(int state) {
        switch (state) {
            case INTERNET_AVAILABLE:
                ServerConnections.getServerConnectionsInstance().registerForServerResponse(this);
                ServerConnections.getServerConnectionsInstance().sendRequest(TYPE_GET_ARTICLES);
                break;
            default:
                break;
        }
    }

    public void registerForArticles(Articles articles) {
        this.articles = articles;
    }

    public interface Articles {
        void onArticlesReady(ArrayList<ArticleVO> articleVOS);
    }

}

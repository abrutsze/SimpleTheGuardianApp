package com.artur.simpleTheGuardianApp.Connections;

import android.support.annotation.NonNull;

import com.artur.simpleTheGuardianApp.BuildConfig;
import com.artur.simpleTheGuardianApp.VO.ArticleVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.artur.simpleTheGuardianApp.Utils.LogsClass.getLogsClassInstance;

/**
 * Created by artur on 02-Mar-19.
 */

public class ServerConnections {

    public static final int TYPE_GET_ARTICLES = 1;
    public static final int ERROR_SERVER_TIMEOUT = 4000;
    public static final int ERROR_NO_ROUTE_TO_HOST_EXCEPTION = 4001;
    public static final int ERROR_UNKNOWN = 4099;
    public static final int WARNING_LAST_ITEM_REACHED = 5001;
    private static ServerConnections serverConnections;
    private final String BASE_URL = "https://content.guardianapis.com/";
    private final String API_KEY_THEGUARDIAN = "073b394f-80c7-4b3e-b3e8-c26ca2eb5b4f";
    private final int ARTICLES_PER_DOWNLOAD = 50;
    private int currentPage = 0, numberOfPages;
    private ServerApi serverApi;
    private ServerResponse serverResponse;

    private ServerConnections() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //create Retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        //Get client and call object for the request
        serverApi = retrofit.create(ServerApi.class);
    }

    public static ServerConnections getServerConnectionsInstance() {
        if (serverConnections == null) {
            serverConnections = new ServerConnections();
        }
        return serverConnections;
    }

    public void sendRequest(final int type) {

        Call<ResponseBody> call = null;
        switch (type) {
            case TYPE_GET_ARTICLES:
                call = serverApi.getArticles(createArticleUrl());
                break;
            default:
                break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                String errorBody = "";
                String rawResponse = "";

                if (!response.isSuccessful()) {
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    rawResponse = response.body() != null ? response.body().string() : null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getLogsClassInstance().i("message= " + response.message());
                getLogsClassInstance().i("body= " + response.body());
                getLogsClassInstance().i("code= " + response.code());
                getLogsClassInstance().i("errorBody= " + errorBody);
                getLogsClassInstance().i("rawResponse= " + rawResponse);

                if (serverResponse != null) {
                    serverResponse.serverResponseStatus(type, response.code(), parseAndSaveResponse(type, rawResponse), errorBody);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (serverResponse == null) return;

                if (t.getMessage().contains("connect timed out") || t.getMessage().contains("timeout")) {
                    serverResponse.serverResponseStatus(type, ERROR_SERVER_TIMEOUT, null, null);
                } else if (t.getMessage().contains("no rout")) {
                    serverResponse.serverResponseStatus(type, ERROR_NO_ROUTE_TO_HOST_EXCEPTION, null, null);
                } else if (t.getMessage().contains("No value for fields")) {
                    serverResponse.serverResponseStatus(type, ERROR_UNKNOWN, null, null);
                } else {
                    serverResponse.serverResponseStatus(type, WARNING_LAST_ITEM_REACHED, null, null);
                }
            }
        });
    }

    public void registerForServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    private ArrayList<Object> parseAndSaveResponse(int type, String result) {
        ArrayList<Object> rawResponseList = null;
        switch (type) {
            case TYPE_GET_ARTICLES:
                rawResponseList = parseAllArticlesJson(result);
                break;
            default:
                break;
        }
        return rawResponseList;
    }

    private ArrayList<Object> parseAllArticlesJson(String result) {
        ArrayList<Object> rawResponseList = new ArrayList<>();
        try {

            JSONObject jsonMainResponse = new JSONObject(result);
            JSONObject jsonResponse = jsonMainResponse.getJSONObject("response");

            numberOfPages = jsonResponse.getInt("pages");

            JSONArray jsonResultsArray = jsonResponse.getJSONArray("results");

            for (int i = 0; i < jsonResultsArray.length(); i++) {

                JSONObject jsonResultObject = jsonResultsArray.getJSONObject(i);

                ArticleVO articleVO = new ArticleVO();

                articleVO.setId(jsonResultObject.getString("id"));
                articleVO.setPublicationDate(jsonResultObject.getString("webPublicationDate"));
                articleVO.setTitle(jsonResultObject.getString("webTitle"));
                articleVO.setCategory(jsonResultObject.getString("pillarName"));

                if (jsonResultObject.has("fields")) {
                    JSONObject jsonFieldObject = jsonResultObject.getJSONObject("fields");
                    articleVO.setImageURL(jsonFieldObject.getString("thumbnail"));
                }

                JSONObject jsonBlocksObject = jsonResultObject.getJSONObject("blocks");
                JSONArray jsonBodyArray = jsonBlocksObject.getJSONArray("body");
                articleVO.setContent(((JSONObject) jsonBodyArray.get(0)).getString("bodyTextSummary"));

                rawResponseList.add(articleVO);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rawResponseList;
    }

    private String createArticleUrl() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        date.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        String dateString = date.format(GregorianCalendar.getInstance().getTime());
        if (currentPage == numberOfPages && currentPage != 0) return null;
        currentPage++;
        getLogsClassInstance().d("currentPage= " + currentPage);
        return BASE_URL + "search?from-date=" + dateString + "&to-date=" + dateString + "&show-fields=thumbnail" + "&page=" + currentPage + "&page-size=" + ARTICLES_PER_DOWNLOAD + "&show-blocks=body" + "&api-key=" + API_KEY_THEGUARDIAN;
    }

    public interface ServerResponse {
        void serverResponseStatus(int type, int responseCode, Object responseMessage, String errorBody);
    }

}

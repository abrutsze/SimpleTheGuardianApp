package com.artur.simpleTheGuardianApp.Connections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by artur on 02-Mar-19.
 */

public interface ServerApi {

    /*Gets today's (London time) articles*/
    @GET
    Call<ResponseBody> getArticles(@Url String url);

}

package kr.ac.skuniv.cosmoslab.multifamilyedu.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * MultiFamilyEdu_Android
 * Class: NetRetrofit
 * Created by youngjun on 2018-11-27.
 * <p>
 * Description: SingleTone
 */

public class NetRetrofit {
    private static final NetRetrofit ourInstance = new NetRetrofit();

    public static NetRetrofit getInstance() {
        return ourInstance;
    }

    private NetRetrofit() {
    }

    /*private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }*/

    private Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://172.30.1.8:8080/")
            .baseUrl("http://54.180.67.243:8080/")
            .addConverterFactory(GsonConverterFactory.create())
//            .client(createOkHttpClient())
            .build();

    NetRetrofitInterface netRetrofitInterface;
    public NetRetrofitInterface getNetRetrofitInterface(){
        if(netRetrofitInterface == null){
            netRetrofitInterface = retrofit.create(NetRetrofitInterface.class);
        }
        return netRetrofitInterface;
    }
}


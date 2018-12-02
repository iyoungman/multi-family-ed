package kr.ac.skuniv.cosmoslab.multifamilyedu.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * MultiFamilyEdu_Android
 * Class: Network
 * Created by hsl95 on 2018-12-02.
 * <p>
 * Description:
 */
public class Network {

    Context context;

    //네트워크 연결상태 확인
    public Boolean confirmNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        return isConnected;
    }

}
package com.artur.simpleTheGuardianApp.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.artur.simpleTheGuardianApp.R;

import java.net.InetAddress;

/**
 * Created by artur on 02-Mar-19.
 */

public class CheckInternet {

    public final static int INTERNET_AVAILABLE = 1;
    private Dialog dialog;
    private InternetState internetState;

    public CheckInternet(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.setTitle(R.string.internet_title);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    click();
                    dialog.dismiss();
                }
            }
        });
    }

    private void click() {
        if (internetState != null) {
            internetState.internetStateChanged(INTERNET_AVAILABLE);
        }
    }

    public boolean isInternetAvailable() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.toString().equals("");

        } catch (Exception e) {
            e.printStackTrace();
            openDialog();
            return false;
        }
    }

    private void openDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void registerForInternetState(InternetState internetState) {
        this.internetState = internetState;
    }

    public interface InternetState {
        void internetStateChanged(int state);
    }

}

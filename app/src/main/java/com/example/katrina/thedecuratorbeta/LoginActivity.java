package com.example.katrina.thedecuratorbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;


import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private PDKClient pdkClient;
    private static final String appID = BuildConfig.AppId;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        pdkClient = PDKClient.configureInstance(this, appID);
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pdkClient.onOauthResponse(requestCode, resultCode, data);
    }

    private void onLogin() {
        List scopes = new ArrayList<String>();

        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

        pdkClient.login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());
                onLoginSuccess();
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.login_button:
                onLogin();
                break;
        }

    }

    private void onLoginSuccess() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }






}

package com.example.manage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private EditText ed_usernaame, ed_password;
    private Button btn_login, btn_exitapp;
    private String jwt_token, access_token;
    SharedPreferences sharedpref;
    static final String shared_pref_key = "Current_Access_Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setActionBarAsRequired();
        findViewByIDs();
    }

    private void findViewByIDs() {
        ed_usernaame = (EditText) findViewById(R.id.edusernme);
        ed_password = (EditText) findViewById(R.id.edpassword);
        btn_login = (Button) findViewById(R.id.btnlogin);
        btn_exitapp = (Button) findViewById(R.id.btnexitapp);
    }

    private void setActionBarAsRequired() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getTitleForActionBar());
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        }
        return false;
    }

    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void showAlertDialogAndTakeActionAccordingly(String title, String message) {
        builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please Confirm !!!");
        builder.setMessage("Exit App?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int k) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        final AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public String getTitleForActionBar() {
        return "I " + new String(Character.toChars(0x2661)) + " Zerion";
    }

    @Override
    public void onBackPressed() {
        showAlertDialogAndTakeActionAccordingly("Please Confirm", "Exit App?");
    }

    private void getAccessTokenAndProceed(String url, String grant_type, String jwt_token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody request_body = new FormBody.Builder()
                .addEncoded("grant_type", grant_type)
                .addEncoded("assertion", jwt_token)
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .post(request_body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        access_token = JsonParser.ParseAccessToken.doJsonParsing(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            proceedFurtherAccordingly(access_token);
                        }
                    });

                }
            }
        });
    }

    private void proceedFurtherAccordingly(String access_token) {
        if (access_token != null && access_token.length() > 0) {
            storeAccessTokenInSharedPreferences(access_token);
            finishAndGoToHomePage();
        } else {
            displayToast("Unable to gain Server Access. Please try again later.");
        }
    }

    private void createAJwtToken() {
        String iss = getResources().getString(R.string.client_key);
        String aud = getResources().getString(R.string.endpoint_token_generate);
        String secret = getResources().getString(R.string.client_secret);
        jwt_token = JJWT.createJWT(iss, aud, secret);

    }

    private void finishAndGoToHomePage() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    ;

    private void storeAccessTokenInSharedPreferences(String access_token) {
        sharedpref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString(shared_pref_key, access_token);
        editor.commit();
    }

    public void loginUser(View view) {
        if (isOnline() && areAllTheFieldsFilled()) {
            createAJwtToken();
            getAccessTokenAndProceed(getResources().getString(R.string.endpoint_token_generate), getResources().getString(R.string.grant_type), jwt_token);
        } else {
            displayToast("Please enter all the fields and check your internet connection to proceed further...");
        }
    }

    private boolean areAllTheFieldsFilled() {
        if (ed_usernaame.getText() != null && ed_usernaame.getText().length() > 0 && ed_password.getText() != null && ed_password.getText().length() > 0) {
            return true;
        }
        return false;
    }

    public void exitApp(View view) {
        finish();
    }
}

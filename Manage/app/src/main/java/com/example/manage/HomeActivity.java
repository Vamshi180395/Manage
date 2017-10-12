package com.example.manage;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences sharedpref;
    ProgressDialog progress_dialog;
    ListView lview_record_ids;
    String profile_id, page_id, access_token, selected_record_id;
    List<String> list_record_ids = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    static final String intent_key_id = "Selected_Record_ID";
    static final String intent_key_token = "TOKEN";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setActionBarAsRequired();
        findViewByIDs();
        initializeGlobals();
        handleOnClicks();
        getMyAccessTokenFromSharedPref();
        getAllRecordIDs();
    }

    private void getMyAccessTokenFromSharedPref() {
        sharedpref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedpref.getString(LoginActivity.shared_pref_key, "");
        if (token != null && token.length() > 0) {
            access_token = token;
        }
    }

    private void setActionBarAsRequired() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getTitleForActionBar());
    }

    private void initializeGlobals() {
        profile_id = getResources().getString(R.string.profile_id);
        page_id = getResources().getString(R.string.page_id);
    }

    private void getAllRecordIDs() {
        if (isOnline() && access_token != null) {
            progress_dialog.show();
            makeAPICall(buildQueryString(getResources().getString(R.string.endpoint_generate_record_ids)), access_token);
        } else {
            displayToast("Please check your internet connection to proceed further..");
        }
    }

    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private String buildQueryString(String url) {
        url = url.replace("[profile_id]", getResources().getString(R.string.profile_id));
        url = url.replace("[page_id]", getResources().getString(R.string.page_id));
        return url;
    }

    private void handleOnClicks() {
        lview_record_ids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_record_id = list_record_ids.get(i);
                GoToRecordDetailsActivity(selected_record_id);
            }
        });
    }

    private void GoToRecordDetailsActivity(String selected_record_id) {
        Intent i = new Intent(HomeActivity.this, RecordActivity.class);
        i.putExtra(intent_key_id, selected_record_id);
        i.putExtra(intent_key_token, access_token);
        startActivity(i);
    }

    private void findViewByIDs() {
        lview_record_ids = (ListView) findViewById(R.id.lview_record_ids);
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setMessage("Loading - Record ID's.");
    }

    private void setListView() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, list_record_ids);
        lview_record_ids.setAdapter(adapter);
    }

    private void makeAPICall(String url, String access_token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .header("Content-Type", "application/json")
                .url(url)
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
                        list_record_ids = JsonParser.ParseRecordIds.doJsonParsing(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewsAccordingly(list_record_ids);
                        }
                    });

                }
            }
        });
    }

    private void setViewsAccordingly(List<String> list_record_ids) {
        progress_dialog.dismiss();
        if (list_record_ids != null && list_record_ids.size() > 0) {
            setListView();
        } else {
            displayToast("Sorry, there are no records available for display.");
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        }
        return false;
    }

    private void showAlertDialogAndTakeActionAccordingly(String title, String message) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Confirm !!!");
        builder.setMessage("Exit App?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int k) {
                finishAndGoToLogin();
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

    private void finishAndGoToLogin() {
        Intent intnt = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intnt);
        finish();
    }

    public String getTitleForActionBar() {
        return "I " + new String(Character.toChars(0x2661)) + " Zerion";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            getAllRecordIDs();
            displayToast("List Refreshed !!!");
        } else {
            finishAndGoToLogin();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        showAlertDialogAndTakeActionAccordingly("Please Confirm", "Do you want to Sign Out?");
    }
}
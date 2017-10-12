package com.example.manage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {
    ProgressDialog progress_dialog;
    TextView txt_name, txt_age, txt_phone, txt_date;
    ImageView img_picture;
    String access_token, profile_id, page_id, selected_record_id;
    Record selected_record = new Record();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setActionBarAsRequired();
        findViewByIDs();
        initializeGlobals();
        handleAnyIntentsPresent();
        getSelectedRecordDetails();
    }

    private void handleAnyIntentsPresent() {
        if (getIntent().getExtras() != null) {
            selected_record_id = getIntent().getExtras().getString(HomeActivity.intent_key_id);
            access_token = getIntent().getExtras().getString(HomeActivity.intent_key_token);
        }
    }

    private void initializeGlobals() {
        profile_id = getResources().getString(R.string.profile_id);
        page_id = getResources().getString(R.string.page_id);
    }

    private void getSelectedRecordDetails() {
        if (isOnline() && access_token != null && selected_record_id != null) {
            progress_dialog.show();
            makeAPICall(buildQueryString(getResources().getString(R.string.endpoint_generate_record_details)), access_token);
        } else {
            displayToast("Something has gone wrong.Please check your internet connection and try again.");
        }
    }

    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String buildQueryString(String url) {
        url = url.replace("[profile_id]", getResources().getString(R.string.profile_id));
        url = url.replace("[page_id]", getResources().getString(R.string.page_id));
        url = url.replace("[record_id]", selected_record_id);
        return url;
    }

    private void findViewByIDs() {
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_age = (TextView) findViewById(R.id.txt_age);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_date = (TextView) findViewById(R.id.txt_date);
        img_picture = (ImageView) findViewById(R.id.img_picture);
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setTitle("Loading - Record Details.");
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
                        selected_record = JsonParser.ParseRecordObjects.doJsonParsing(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RecordActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillRecordDetails(selected_record);
                        }
                    });

                }
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return true;
        }
        return false;
    }

    private void fillRecordDetails(Record selected_record) {
        progress_dialog.dismiss();
        if (selected_record != null) {
            txt_name.setText(selected_record.getName().toString());
            txt_age.setText(selected_record.getAge().toString());
            txt_phone.setText(selected_record.getPhone().toString());
            txt_date.setText(selected_record.getDate().toString());
            Picasso.with(img_picture.getContext()).load(selected_record.getPicture()).into(img_picture);
        } else {
            displayToast("Unable to retreive record details. Please try again.");
        }
    }

    private void setActionBarAsRequired() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getTitleForActionBar());
    }

    public String getTitleForActionBar() {
        return "I " + new String(Character.toChars(0x2661)) + " Zerion";
    }
}



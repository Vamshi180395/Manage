package com.example.manage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rama Vamshi Krishna on 09/24/2017.
 */
public class JsonParser {
    static class ParseRecordIds {
        static ArrayList<String> doJsonParsing(String inputstring) throws JSONException {
            JSONArray root_array = new JSONArray(inputstring);
            if (root_array != null && root_array.length() > 0) {
                ArrayList<String> list_record_ids = new ArrayList<>();
                for (int i = 0; i < root_array.length(); i++) {
                    JSONObject record_id_obj = root_array.getJSONObject(i);
                    list_record_ids.add(record_id_obj.getString("id"));
                }
                return list_record_ids;
            }
            return null;
        }
    }

    static class ParseRecordObjects {
        static Record doJsonParsing(String inputstring) throws JSONException {
            Record record_obj = new Record();
            JSONObject root_object = new JSONObject(inputstring);
            if (root_object != null) {
                record_obj.setName(root_object.optString("my_name"));
                record_obj.setAge(root_object.optString("my_age"));
                record_obj.setPhone(root_object.optString("my_phone"));
                record_obj.setPicture(root_object.optString("my_picture"));
                record_obj.setDate(root_object.optString("my_date"));
                return record_obj;
            }
            return null;
        }
    }

    static class ParseAccessToken {
        static String doJsonParsing(String inputstring) throws JSONException {
            JSONObject root_object = new JSONObject(inputstring);
            if (root_object != null && !root_object.has("error") && root_object.has("access_token")) {
                return root_object.getString("access_token");
            }
            return null;
        }
    }
}

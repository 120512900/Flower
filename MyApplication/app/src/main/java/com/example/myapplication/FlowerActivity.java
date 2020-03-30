package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.myapplication.MainActivity.getBitmapFromUri;

public class FlowerActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView picture;
    private ArrayList<Flower> flowers;

    //private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower);
        // fab = findViewById(R.id.fab);
        tv = findViewById(R.id.tv);
        picture = findViewById(R.id.picture);
        Intent intent = getIntent();
        String version = intent.getStringExtra("version");
        if (version.equals("camera")) {
            // String response=intent.getStringExtra("response");
            String uri = intent.getStringExtra("uri");
            //getBitmapFromUri(this, getImageContentUri(this,path));
            Bitmap bitmap2 = null;
            try {
                bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(uri)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            picture.setImageBitmap(bitmap2);
        } else {

            String path = intent.getStringExtra("path");
            final Bitmap bitmap2 = getBitmapFromUri(this, getImageContentUri(this, path));
            picture.setImageBitmap(bitmap2);
        }

        //tv.setText(response+path);
        String response = intent.getStringExtra("response");
        try {
            JSONObject jsonObject = new JSONObject(response);
            response = jsonObject.getString("result");
            Log.d("TAG", "onCreate: " + response);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            parseFlowerJson(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseFlowerJson(String jsonData) throws JSONException {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        flowers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Flower flower = new Flower();

            flower.setScore(jsonObject.getString("score"));
            String name1 = jsonObject.getString("name");
            if (name1.equals("非植物")) {
                name1 = name1 + "NO";
            }
            flower.setName(name1);
            flower.setBaike_info(jsonObject.getString("baike_info"));
            Log.d("TAG", "parseFlowerJson: " + flower.getName() + flower.getScore());
            flowers.add(flower);
        }

        FlowerAdapter adapter = new FlowerAdapter(FlowerActivity.this, R.layout.flower_item, flowers);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Flower flower = flowers.get(position);
                if (flower.getName().equals("非植物NO")) return;
                if (flower.getBaikeInfo().getBaike_url() == null) {
                    Intent intent = new Intent(FlowerActivity.this, FlowerWebActivity.class);
                    intent.putExtra("name", flower.getName());
                    intent.putExtra("url", "http://www.baike.com/gwiki/" + flower.getName());
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(FlowerActivity.this, FlowerInfoActivity.class);
                intent.putExtra("name", flower.getName());
                intent.putExtra("info", flower.getBaike_info());
                startActivity(intent);
            }
        });


    }

    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}

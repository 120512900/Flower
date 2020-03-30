package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {
    private ArrayList<Info> infos;
    private MyHelper myHelper;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        btn = findViewById(R.id.btn);
        Log.d("TAG", "onCreate: Info");
        myHelper = new MyHelper(this);
        SQLiteDatabase db;
        ContentValues values;
        infos = new ArrayList<>();
        db = myHelper.getWritableDatabase();
        Cursor cursor = db.query("info", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst();
            Info info = new Info();
            info.setName(cursor.getString(0));
            info.setUrl(cursor.getString(1));
            info.setBaike_url(cursor.getString(2));
            infos.add(info);
        }
        while (cursor.moveToNext()) {
            Info info = new Info();
            info.setName(cursor.getString(0));
            info.setUrl(cursor.getString(1));
            info.setBaike_url(cursor.getString(2));
            infos.add(info);

        }
        cursor.close();
        db.close();
        final InfoAdapter adapter = new InfoAdapter(InfoActivity.this, R.layout.info_item, infos);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Info info = infos.get(position);
                //if(info.getName().equals("非植物NO"))return;
                if (info.getBaike_url() == null) {
                    Intent intent = new Intent(InfoActivity.this, FlowerWebActivity.class);
                    intent.putExtra("name", info.getName());
                    intent.putExtra("url", "http://www.baike.com/gwiki/" + info.getName());
                    startActivity(intent);
                    return;
                } else {
                    Intent intent = new Intent(InfoActivity.this, FlowerWebActivity.class);
                    intent.putExtra("name", info.getName());
                    intent.putExtra("url", info.getBaike_url());
                    startActivity(intent);
                    return;
                }

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db;
                ContentValues values;
                db = myHelper.getWritableDatabase();
                db.delete("info", null, null);
                Toast.makeText(InfoActivity.this, "OK", Toast.LENGTH_SHORT).show();
                db.close();
                startActivity(new Intent(InfoActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}

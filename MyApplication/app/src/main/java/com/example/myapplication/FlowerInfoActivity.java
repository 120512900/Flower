package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlowerInfoActivity extends AppCompatActivity {
    private Baike_info baikeInfo = new Baike_info();
    private ImageView picture;
    private TextView tv;
    private Button btn;
    private String url;
    static String to;
    private MyHelper myHelper;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_info);
        picture = (ImageView) findViewById(R.id.picture);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn);
        fab = findViewById(R.id.fab);
        myHelper = new MyHelper(this);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String info = intent.getStringExtra("info");
        try {
            setInfo(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(this).load(baikeInfo.getImage_url()).into(picture);
        tv.setText(baikeInfo.getDescription());
        t(baikeInfo.getDescription());
        url = baikeInfo.getBaike_url();
        Log.d("TAG,", "onCreate:000000 " + baikeInfo.getBaike_url());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG,", "onCreate: " + "url");
                Intent intent = new Intent(FlowerInfoActivity.this, FlowerWebActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("url", url);

                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //name url;
                SQLiteDatabase db;
                ContentValues values;


                String myname = name;
                String myurl = baikeInfo.getImage_url();

                db = myHelper.getWritableDatabase();
                values = new ContentValues();
                values.put("name", myname);
                values.put("url", myurl);
                values.put("Baike", url);
                db.insert("info", null, values);
                Toast.makeText(FlowerInfoActivity.this, "OK", Toast.LENGTH_SHORT).show();
                // Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                // Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                db.close();

            }
        });
    }


    public void setInfo(String info) throws JSONException {
        // Log.d("TAG", "getBaikeInfo: ");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(info);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        Log.d("TAG", "getBaikeInfo: 111111111111111111111111");
        baikeInfo.setImage_url(jsonObject.getString("image_url"));
        baikeInfo.setBaike_url(jsonObject.getString("baike_url"));
        baikeInfo.setDescription(jsonObject.getString("description"));
    }

    public void t(String word) {
        final String[] result = new String[1];
        String from = "auto";//源语种 en 英语 zh 中文
        if (word.length() == word.getBytes().length) {//成立则说明没有汉字，否则由汉字。
            to = "zh"; //没有汉字 英译中
        } else {
            to = "en";//含有汉字 中译英
        }
        String appid = "20200327000406758";//appid 管理控制台有
        String salt = (int) (Math.random() * 100 + 1) + "";//随机数 这里范围是[0,100]整数 无强制要求
        String key = "d22FggKXHbaNwZWllapn";//密钥 管理控制台有
        String string1 = appid + word + salt + key;// string1 = appid+q+salt+密钥
        String sign = MD5Utils.getMD5Code(string1);// 签名 = string1的MD5加密 32位字母小写
        Retrofit retrofitBaidu = new Retrofit.Builder()
                .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        BaiduTranslateService baiduTranslateService = retrofitBaidu.create(BaiduTranslateService.class);
        Call<RespondBean> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
        call.enqueue(new Callback<RespondBean>() {
            @Override
            public void onResponse(Call<RespondBean> call, Response<RespondBean> response) {
                //请求成功
                Log.d("TAG", "onResponse: 请求成功");
                RespondBean respondBean = response.body();//返回的JSON字符串对应的对象
                result[0] = respondBean.getTrans_result().get(0).getDst();//获取翻译的字符串String
                tv.append("\n" + result[0]);
            }
            @Override
            public void onFailure(Call<RespondBean> call, Throwable t) {
                //请求失败 打印异常
                Log.d("TAG", "onResponse: 请求失败 " + t);
            }
        });

    }
}

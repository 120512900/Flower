package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

public class FlowerAdapter extends ArrayAdapter<Flower> {

    private int resourceId;
    private Context mcontext;


    public FlowerAdapter(Context context, int textViewResourceId,

                         List<Flower> objects) {

        super(context, textViewResourceId, objects);
        this.mcontext = context;
        resourceId = textViewResourceId;

    }


    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        Flower flower = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.Image = (ImageView) view.findViewById(R.id.image);
            viewHolder.Name = (TextView) view.findViewById(R.id.name);
            viewHolder.Score = (TextView) view.findViewById(R.id.score);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        String url = flower.getBaikeInfo().getImage_url();
        //  url="http://imgsrc.baidu.com/baike/pic/item/08f790529822720e73cd693b79cb0a46f31fabf4.jpg";
        Log.d("TAG", "getView: " + url);
        Glide.with(mcontext).load(url).into(viewHolder.Image);
        // viewHolder.Image.setImageResource();
        viewHolder.Name.setText(flower.getName());
        float a = Float.parseFloat(flower.getScore()) * 100;
        float b = (float) (Math.round(a * 100)) / 100;
        String sco = (b) + "";
        viewHolder.Score.setText(sco + "%");
        return view;
    }


    class ViewHolder {


        ImageView Image;

        TextView Score;

        TextView Name;


    }
}

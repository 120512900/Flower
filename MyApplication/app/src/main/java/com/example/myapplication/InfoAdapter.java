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

public class InfoAdapter extends ArrayAdapter<Info> {
    private int resourceId;
    private Context mcontext;

    public InfoAdapter(@NonNull Context context, int resource, @NonNull List<Info> objects) {
        super(context, resource, objects);
        this.mcontext = context;
        resourceId = resource;

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        Info info = getItem(position); // 获取当前项的Fruit实例

        View view;

        InfoAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            viewHolder = new InfoAdapter.ViewHolder();

            viewHolder.Image = (ImageView) view.findViewById(R.id.image);

            viewHolder.Name = (TextView) view.findViewById(R.id.name);
            view.setTag(viewHolder); // 将ViewHolder存储在View中

        } else {

            view = convertView;

            viewHolder = (InfoAdapter.ViewHolder) view.getTag(); // 重新获取ViewHolder

        }
        String url = info.getUrl();

        Log.d("TAGINFO", "getView: " + url);
        //url="http://imgsrc.baidu.com/baike/pic/item/08f790529822720e73cd693b79cb0a46f31fabf4.jpg";
        Glide.with(mcontext).load(url).into(viewHolder.Image);
        // viewHolder.Image.setImageResource();


        viewHolder.Name.setText(info.getName());


        return view;

    }


    class ViewHolder {
        ImageView Image;
        TextView Name;
    }
}

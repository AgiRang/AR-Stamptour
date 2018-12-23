package com.example.administrator.sar;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PagerAdapter extends android.support.v4.view.PagerAdapter {

    Context context;
    List<PagerModel> pagerArr;
    LayoutInflater inflater;
    ImageView pagerImage;

    //private FirebaseDatabase database;

    public PagerAdapter(Context context, List<PagerModel> pagerArr) {
        this.context = context;
        this.pagerArr = pagerArr;
        inflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_list_item, container, false);
        //TextView tv = (TextView) view.findViewById(R.id.textview);
        pagerImage = (ImageView) view.findViewById(R.id.pagerImage);


        view.setTag(position);
        ((ViewPager) container).addView(view);
        PagerModel model = pagerArr.get(position);
        //tv.setText(model.getTitle());
        Glide.with(view).load(model.getImageUri()).into(pagerImage);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return pagerArr.size();
    }
}

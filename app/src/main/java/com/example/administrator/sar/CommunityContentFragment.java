package com.example.administrator.sar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommunityContentFragment extends Fragment {
    TextView content_UserName;
    TextView content_Date;
    TextView content_text;
    TextView content_Title;
    ImageView content_Profile;
    ImageView content_Image;

    private FirebaseDatabase database;
    private FirebaseAuth auth;

    String key;

    public CommunityContentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.community_content, container, false);
        content_UserName = (TextView) layout.findViewById(R.id.Content_UserName);
        content_Date = (TextView) layout.findViewById(R.id.Content_Date);
        content_text = (TextView) layout.findViewById(R.id.Content_Text);
        content_Title = (TextView) layout.findViewById(R.id.Content_Title);
        content_Profile = (ImageView) layout.findViewById(R.id.Content_ProfileImage);
        content_Image = (ImageView) layout.findViewById(R.id.Content_Image);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        database.getReference().child("Community").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.getKey() == key) {
                        CommunityItem communityItem = snapshot.getValue(CommunityItem.class);
                        content_Date.setText(communityItem.itemdate);
                        content_text.setText(communityItem.itemcontent);
                        content_Title.setText(communityItem.itemTitle);
                        content_UserName.setText(communityItem.userName);
                        Glide.with(layout).load(communityItem.imageUrl).into(content_Image);
                        /*
                        if (auth.getCurrentUser().getPhotoUrl() != null){
                            Glide.with(layout).load(auth.getCurrentUser().getPhotoUrl()).into(content_Profile);
                            //ProfileImage.setImageURI(auth.getCurrentUser().getPhotoUrl());
                        }else{
                            Glide.with(layout).load(R.drawable.profile).into(content_Profile);
                        }
                        */
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(" Community");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        key = getArguments().getString("userId");

    }
}

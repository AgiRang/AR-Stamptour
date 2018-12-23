package com.example.administrator.sar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {
    Button writeBtn;
    Fragment fragment;
    View layout;
    private RecyclerView recyclerView;
    private List<CommunityItem> communityItemList = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase database;
    CommunityRecyclerViewAdapter communityRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.community_layout, container, false);
        writeBtn = (Button) layout.findViewById(R.id.wirteBtn);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        communityRecyclerViewAdapter = new CommunityRecyclerViewAdapter();
        recyclerView.setAdapter(communityRecyclerViewAdapter);
        recyclerView.setAdapter(communityRecyclerViewAdapter);

        database.getReference().child("Community").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                communityItemList.clear();
                uidLists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CommunityItem communityItem = snapshot.getValue(CommunityItem.class);
                    communityItemList.add(communityItem);

                    String uidKey = snapshot.getKey();
                    uidLists.add(uidKey);
                }
                communityRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new write();

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.content_main, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
        return layout;
    }

    class CommunityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item, parent, false);
            //layout = inflater.inflate(R.layout.community_item, container, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder) holder).item_title.setText(communityItemList.get(position).itemTitle);
            ((CustomViewHolder) holder).item_userName.setText(communityItemList.get(position).userName);
            ((CustomViewHolder) holder).item_date.setText(communityItemList.get(position).itemdate);
            Glide.with(((CustomViewHolder) holder).item_image.getContext()).load(communityItemList.get(position).imageUrl).into(((CustomViewHolder) holder).item_image);
            ((CustomViewHolder) holder).CommunityItem_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fragment = new CommunityContentFragment();
                    Bundle bundle = new Bundle(1); // 파라미터는 전달할 데이터 개수
                    bundle.putString("userId", uidLists.get(position)); // key , value

                    fragment.setArguments(bundle);

                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.content_main, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return communityItemList.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView item_image;
            TextView item_title;
            TextView item_userName;
            TextView item_date;
            //LinearLayout CommunityItem_Layout;
            ConstraintLayout CommunityItem_Layout;

            public CustomViewHolder(View view) {
                super(view);

                item_image = (ImageView) view.findViewById(R.id.CommunityItem_image);
                item_title = (TextView) view.findViewById(R.id.CommunityItem_title);
                item_userName = (TextView) view.findViewById(R.id.CommunityItem_username);
                item_date = (TextView) view.findViewById(R.id.CommunityItem_date);
                CommunityItem_Layout = (ConstraintLayout) view.findViewById(R.id.CommunityItem_Layout);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Community");
    }
}
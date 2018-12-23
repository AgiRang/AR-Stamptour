package com.example.administrator.sar;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class write extends Fragment {
    Button writeOk;
    Button writeCancel;
    Button selectImageBtn;
    TextView ImagePath_TextView;
    EditText titleEdit;
    EditText contentEdit;
    String imagePath;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    long mNow;
    Date mDate;

    private static final int GALLERY_CODE = 10;

    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String TAG = "write-";
    ActionBar actionBar;
    Intent intent;
    Uri file;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.community_write, container, false);

        actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        writeOk = (Button) layout.findViewById(R.id.writeOk);
        writeCancel = (Button) layout.findViewById(R.id.writeCancel);
        selectImageBtn = (Button) layout.findViewById(R.id.selectImageBtn);
        ImagePath_TextView = (TextView) layout.findViewById(R.id.ImagePath_TextView);
        titleEdit = (EditText) layout.findViewById(R.id.writeTitle_EditText);
        contentEdit = (EditText) layout.findViewById(R.id.writeContent_EditText);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        writeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titleEdit.length() == 0){
                    Toast.makeText(getActivity(),"제목을 입력하세요.",Toast.LENGTH_SHORT).show();
                    titleEdit.requestFocus();
                    return;
                }
                if(contentEdit.length() == 0){
                    Toast.makeText(getActivity(),"내용을 입력하세요.",Toast.LENGTH_SHORT).show();
                    contentEdit.requestFocus();
                    return;
                }
                if(ImagePath_TextView.getText().toString().equals("이미지 첨부") || ImagePath_TextView.getText().toString().equals("이미지를 선택하세요.")){
                    Toast.makeText(getActivity(),"이미지를 선택하세요.",Toast.LENGTH_SHORT).show();
                    ImagePath_TextView.setText("이미지를 선택하세요.");
                    return;
                }
                //writeCheckError();
                upload();

            }
        });
        writeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.setHomeAsUpIndicator(R.drawable.stamp);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();

            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Community");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE) {
            imagePath = getPath(data.getData());
            System.out.println(imagePath);
            ImagePath_TextView.setText(imagePath);
            file = Uri.fromFile(new File(imagePath));
            //Log.d(TAG, "getFileName: "+getFileName(file)); //업로드하는 사진의 파일이름 가져오기 string
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    private void upload() {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://stampar-56ecb.appspot.com");
        final StorageReference riversRef = storageRef.child("images/community/" + file.getLastPathSegment());
        Log.d(TAG,"storage: "+riversRef);
        Log.d(TAG,"storage: "+riversRef.getDownloadUrl());


        UploadTask uploadTask = riversRef.putFile(file);
        Log.d(TAG,"storage2: "+riversRef.getDownloadUrl());

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                Log.d(TAG,"Task<Uri>: "+riversRef.getDownloadUrl());
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG,"onComplete: "+downloadUri);
                    imagePath = ""+downloadUri;

                    CommunityItem communityItem = new CommunityItem();
                    communityItem.imageUrl = imagePath;
                    communityItem.itemTitle = titleEdit.getText().toString();
                    communityItem.itemcontent = contentEdit.getText().toString();
                    communityItem.itemdate = getTime();
/*
                    if(auth.getCurrentUser().getDisplayName().length() != 0){
                        Log.d(TAG,"getDisplayName: true");
                        communityItem.userName = auth.getCurrentUser().getDisplayName();
                    }else {
                        Log.d(TAG,"getDisplayName: false");
                        communityItem.userName = "익명";
                    }
*/
                    database.getReference().child("Community").push().setValue(communityItem);
                    actionBar.setHomeAsUpIndicator(R.drawable.stamp);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();

                } else {

                    // Handle failures
                    // ...
                }
            }
        });


// Register observers to listen for when the download is done or if it fails
        /*
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                //@SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG,"addOnFailureListener: "+imagePath);
            }
        });
*/
    }

    private void writeCheckError(){
        if(titleEdit.length() == 0){
            Toast.makeText(getActivity(),"제목을 입력하세요.",Toast.LENGTH_SHORT).show();
            titleEdit.requestFocus();
            //return true;
        }
        if(contentEdit.length() == 0){
            Toast.makeText(getActivity(),"내용을 입력하세요.",Toast.LENGTH_SHORT).show();
            contentEdit.requestFocus();
            //return true;
        }
        if(imagePath.length() == 0){
            Toast.makeText(getActivity(),"이미지를 선택하세요.",Toast.LENGTH_SHORT).show();
            ImagePath_TextView.setText("이미지를 선택하세요.");
            //return true;
        }
        //return false;
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
/*
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            //Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
    */
}
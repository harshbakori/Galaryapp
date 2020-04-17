package com.example.galary;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.System.load;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final int REQUEST_PERMISSION=1234;

    private static final String[] PERMISSIONS ={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_COUNT=2;

    @SuppressLint("NewApi")
    private boolean permissionnotgrant() {
        for (int i = 0; i < PERMISSION_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(final int requestcode,final String [] permissions,final int[] grantresult)
    {
        super.onRequestPermissionsResult(requestcode,permissions,grantresult);
        if(requestcode==REQUEST_PERMISSION && grantresult.length>0)
        {
            if(permissionnotgrant()){
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
            else {onResume();}
        }
    }

    private boolean isGalaryInitialized;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionnotgrant()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSION);
            return;
        }
        //initialize app
        if (!isGalaryInitialized) {
            final ListView listView = findViewById(R.id.listView);
            final GallaryAdapter gallaryAdapter = new GallaryAdapter();
            final File imageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            final File[] files=imageDir.listFiles();
            final int filecount = files.length;
            final List<String> fileList = new ArrayList<>();
            for(int i=0; i<filecount; i++){
                final String path = files[i].getAbsolutePath();
                if(path.endsWith(".jpg")||path.endsWith(".png")){
                    fileList.add(path);
                }
            }

            gallaryAdapter.setData(fileList);
            listView.setAdapter(gallaryAdapter);
            isGalaryInitialized = true;
        }
    }

    final class GallaryAdapter extends BaseAdapter{

        private List<String> data = new ArrayList<>();
        void setData(List<String> data) {
            if(this.data.size()>0)
            {
                data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if(convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            }
            else {
                imageView = (ImageView)convertView;
            }
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageView);
            return null;
        }
    }
}

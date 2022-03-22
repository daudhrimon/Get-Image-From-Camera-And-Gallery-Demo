package com.daud.get_imagefrom_cameraandgallery_demo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Button btn;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private String ACTION = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        btn = findViewById(R.id.btn);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 0);
        }


        btn.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_layout,null);
            alertDialog.setView(view1);
            ImageView camera = view1.findViewById(R.id.camera);
            ImageView gallery = view1.findViewById(R.id.gallery);

            camera.setOnClickListener(view2 -> {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
                alertDialog.dismiss();
                ACTION = "CAMERA";
            });

            gallery.setOnClickListener(view2 -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                activityResultLauncher.launch(intent);
                alertDialog.dismiss();
                ACTION = "GALLERY";
            });
            alertDialog.setCancelable(false);
            alertDialog.show();


        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result!=null){
                    if (ACTION.equals("CAMERA")){
                        Bundle extras = result.getData().getExtras();
                        Bitmap imgBitmap = (Bitmap) extras.get("data");
                        Uri imgUri = getImageUri(MainActivity.this,imgBitmap);
                        img.setImageURI(imgUri);
                        ///////////////////////////////////////////
                    } else if (ACTION.equals("GALLERY")){
                        Uri imgUri = result.getData().getData();
                        img.setImageURI(imgUri);
                        ///////////////////////////////////////////
                    }

                }
            }
        });

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
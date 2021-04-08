package com.android.oneclick.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.oneclick.R;

public class ScreenshotPreviewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_preview);

        ImageView imageView = findViewById(R.id.preview_image);
        try {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bmp);

//           // Load the animation like this
//            Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
//                    R.anim.demo_animation);
//// Start the animation like this
//            imageView.startAnimation(animSlide);
        }catch(Exception ex){

        }

    }

}
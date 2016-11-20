package com.example.nayib.camprueba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

public class UploadActivity extends AppCompatActivity {
    ImageView imgUpload;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Button btnFacebook;
    Bitmap result;
    SharePhotoContent photoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.activity_upload);
        btnFacebook= (Button) findViewById(R.id.btnFacebook);
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        String path = getIntent().getStringExtra("foto");
        result = BitmapFactory.decodeFile(path);
        imgUpload.setImageBitmap(result);

        Toast.makeText(UploadActivity.this, "click", Toast.LENGTH_SHORT).show();


        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(result)
                .build();

        photoContent = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#GatosUninorte")
                        .build())
                .build();


        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setShareContent(photoContent);


        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareDialog.show(photoContent);
            }
        });

    }






    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

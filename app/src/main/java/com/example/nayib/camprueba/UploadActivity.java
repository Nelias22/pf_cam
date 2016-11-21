package com.example.nayib.camprueba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.io.File;

import io.fabric.sdk.android.Fabric;

import static com.example.nayib.camprueba.MainActivity.TWITTER_KEY;
import static com.example.nayib.camprueba.MainActivity.TWITTER_SECRET;

public class UploadActivity extends AppCompatActivity {
    ImageView imgUpload;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Button btnFacebook;
    Button btnTwitter;
    EditText etMessage;
    Spinner spinGatos;
    String path;
    Bitmap result;
    SharePhotoContent photoContent;
    private Bundle intentExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.activity_upload);
        btnFacebook= (Button) findViewById(R.id.btnFacebook);
        btnTwitter= (Button) findViewById(R.id.btnTwitter);
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        path = getIntent().getStringExtra("foto");
        result = BitmapFactory.decodeFile(path);
        imgUpload.setImageBitmap(result);

        //Populate spinner

        spinGatos = (Spinner) findViewById(R.id.spinGatos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gatos_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGatos.setAdapter(adapter);

        //Facebook
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(result)
                .build();

        photoContent = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#GatosUninorte")
                        .build())
                .build();


        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "click", Toast.LENGTH_SHORT).show();
                shareDialog.show(photoContent);
            }
        });

        //Twitter
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "click twitter", Toast.LENGTH_SHORT).show();
                clickTwitter();
            }
        });
    }

    public class MyResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
                // success
                final Long tweetId = intentExtras.getLong(TweetUploadService.EXTRA_TWEET_ID);
            } else {
                // failure
               final Intent retryIntent = intentExtras.getParcelable(TweetUploadService.EXTRA_RETRY_INTENT);
            }
        }
    }
    public void clickTwitter(){
        Toast.makeText(this, spinnerResult(), Toast.LENGTH_SHORT).show();
        etMessage= (EditText) findViewById(R.id.etMessage);
        String message= etMessage.getText().toString();
        File zaFile= new File(path);
        Uri myImageUri= Uri.fromFile(zaFile);
        try{
            TweetComposer.Builder builder = new TweetComposer.Builder(this)
                    .text(message + " #GatosUninorte " + spinnerResult())
                    .image(myImageUri);
            builder.show();
        }catch(Exception e){
            Toast.makeText(UploadActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    public String spinnerResult(){
        String spinResult="", spinOp;
        spinOp=spinGatos.getSelectedItem().toString();

        switch (spinOp){
            case "Selecciona uno":
                spinResult="";
                break;
            case "No lo conozco":
                spinResult="#NoLoConozco";
                break;
            default:
                spinResult= "#"+spinOp;
                break;
        }
        return spinResult;
    }



    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

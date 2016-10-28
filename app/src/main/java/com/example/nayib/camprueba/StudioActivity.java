package com.example.nayib.camprueba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nayib.camprueba.view.StickerView;

import java.util.ArrayList;


public class StudioActivity extends AppCompatActivity {
    RelativeLayout content_studio;
    ImageView studioImg;
    View mAddSticker;
    StickerView mCurrentView;
    ArrayList<View> mViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        studioImg= (ImageView) findViewById(R.id.studioImg);
        content_studio= (RelativeLayout) findViewById(R.id.content_studio);
        mAddSticker = findViewById(R.id.btnSticker);
        mViews = new ArrayList<>();
        setSupportActionBar(toolbar);
        studioImg.setImageBitmap(null);

        Intent data = getIntent();

        /*byte[] bytes = data.getByteArrayExtra("BMP");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        studioImg.setImageBitmap(bmp);*/

        String path=data.getStringExtra("src");
        studioImg.setImageBitmap(BitmapFactory.decodeFile(path));


        //Toast.makeText(this, "I changed the picture", Toast.LENGTH_SHORT).show();

        mAddSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStickerView();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void addStickerView() {
        final StickerView stickerView = new StickerView(this);
        stickerView.setImageResource(R.drawable.winterhat);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mViews.remove(stickerView);
                content_studio.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {

                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        content_studio.addView(stickerView, lp);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
    }

    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }

        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

}

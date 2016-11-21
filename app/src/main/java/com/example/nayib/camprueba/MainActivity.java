package com.example.nayib.camprueba;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "rmkbDACg05OnikTB5QEdhzXEl";
    public static final String TWITTER_SECRET = "iVFBkrvt1QGP4K0IWApmg9oyJT4XNX0iMRmVp6wdV4823EIDtS";

    // Aquí definimos los resultados del intent, los números son random (creo)
    private static final int CAMERA_PIC_REQUEST = 22;
    private static final int RESULT_LOAD_IMAGE = 33 ;
    private static final int LOAD_FOR_EDIT= 44 ;

    Uri cameraUri;
    //Definiciones de los botones para los OnClickListener
    Button BtnSelectImg;
    Button BtnGallery;
    Button BtnStudio;
    TextView TxtSelectImg;
    TextView TxtGallery;
    TextView TxtStudio;
    private ImageView ImgPhoto;
    Uri mLocationForPhotos= Images.Media.EXTERNAL_CONTENT_URI;
    File filepath =Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
    String currentImgPath = null;



    private String targetFilename="img.jpg";
    //private String Camerapath ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Inicializar Facebook
        //FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main);
        //Asignar los elementos del layout
        ImgPhoto = (ImageView) findViewById(R.id.ImgPhoto);
        BtnSelectImg = (Button) findViewById(R.id.BtnSelectImg);
        BtnGallery=(Button) findViewById(R.id.BtnGallery);
        BtnStudio=(Button) findViewById(R.id.BtnStudio);
        TxtSelectImg = (TextView) findViewById(R.id.TxtSelectImg);
        TxtGallery=(TextView) findViewById(R.id.TxtGallery);
        TxtStudio=(TextView) findViewById(R.id.TxtStudio);

        //OnClickListener para tomar la foto
        BtnSelectImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
               openCamera();
            }
        });
        //OnClickListener para abrir la galería
        BtnGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
               openGallery();
            }
        });
        //OnClickListener para abrir nuestro estudio de edición.
        BtnStudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
               openStudio();
            }
        });

        //Para que el texto también funcione como botón

        //OnClickListener para tomar la foto
        TxtSelectImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                openCamera();
            }
        });
        //OnClickListener para abrir la galería
        TxtGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                openGallery();
            }
        });
        //OnClickListener para abrir nuestro estudio de edición.
        TxtStudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                openStudio();
            }
        });


    }


    @Override
    //Qué hacer cuando se recibe un resultado de un intent.
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {  /*Con el switch, revisamos qué requestCode se le pasó al intent
                                      y dependiendo de eso llamamos a las funciones que necesitamos.*/
                case CAMERA_PIC_REQUEST:
                    if (resultCode == RESULT_OK) { //en todos los casos se revisa si la actividad que llamamos se completó con éxito.
                        try {
                            /*La cámara regresa la información de la foto como un extra,
                            le hacemos casting como Bitmap y guardamos en una variable.
                            Luego, se asigna este Bitmap al ImageView
                             */
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            ImgPhoto.setImageBitmap(photo);



                            // Create a new folder in SD Card
                            File dir = new File(filepath.getAbsolutePath()
                                    + "/GatosUninorte/");
                            dir.mkdirs();

                            generateImgName();
                            File file = new File(dir, targetFilename );

                            OutputStream output = new FileOutputStream(file);

                            // Compress into png format image from 0% - 100%
                            photo.compress(Bitmap.CompressFormat.PNG, 100, output);
                            output.flush();
                            output.close();

                            //String url=MediaStore.Images.Media.insertImage(getContentResolver(), file.getPath(), targetFilename , "Foto tomada con el app de Gatos Uninorte");
                            //Toast.makeText(this, url, Toast.LENGTH_LONG).show();

                            // Tell the media scanner about the new file so that it is
                            // immediately available to the user.
                            MediaScannerConnection.scanFile(this,
                                    new String[] { file.toString() }, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.i("ExternalStorage", "Scanned " + path + ":");
                                            Log.i("ExternalStorage", "-> uri=" + uri);
                                        }
                                    });
                            currentImgPath=file.getPath();

                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case RESULT_LOAD_IMAGE:
                    if (resultCode == RESULT_OK) {
                        try {
                            if (null != data) {
                                Uri selectedImage = data.getData(); //Obtenemos el URI (Unified Resource Identifier) de la imagen
                                String picturePath=getRealPathFromURI(getApplicationContext(),selectedImage);

                                ImgPhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                //Creamos un Bitmap a partir de los datos de la imagen y la asignamos al ImageView
                                currentImgPath=picturePath;
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                /*case LOAD_FOR_EDIT:
                    if (resultCode == RESULT_CANCELED) {
                        try {
                            finishActivity(LOAD_FOR_EDIT);
                        }
                        catch(Exception e){
                            Toast.makeText(this, "Didn't work", Toast.LENGTH_LONG).show();
                        };
                    }
                    break;*/
                default:
                    break;
            }
        } catch (Exception e) {
        }
    }


    public void generateImgName(){

        String number = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        targetFilename= "img_"+number+".jpg";

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void openGallery(){
        try {  //Llamamos a la galería y esperamos que el usuario escoja una foto.
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, mLocationForPhotos);
            startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
        }
    }
    private void openCamera(){
        try {  //Llamamos a la cámara con un intent y esperamos la foto.
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //generateImgName();

            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(mLocationForPhotos, targetFilename));
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't load camera", Toast.LENGTH_LONG).show();
        }
    }
    private void openStudio(){
        try {  //Llamamos a la actividad del estudio y le pasamos la imagen.

            if(currentImgPath==null){
                Toast.makeText(MainActivity.this, "Por favor, toma o selecciona una foto.", Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(MainActivity.this, currentImgPath, Toast.LENGTH_SHORT).show();
                Intent studioIntent = new Intent(getApplicationContext(),StudioActivity.class);
                studioIntent.putExtra("src",currentImgPath);
                startActivity(studioIntent);
            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't open studio", Toast.LENGTH_LONG).show();
        }
    }


}
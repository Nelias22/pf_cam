package com.example.nayib.camprueba;
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
import android.widget.Toast;

import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends Activity {
    // Aquí definimos los resultados del intent, los números son random (creo)
    private static final int CAMERA_PIC_REQUEST = 22;
    private static final int RESULT_LOAD_IMAGE = 33 ;

    Uri cameraUri;
    //Definiciones de los botones para los OnClickListener
    Button BtnSelectImg;
    Button BtnGallery;
    Button BtnStudio;
    private ImageView ImgPhoto;
    Uri mLocationForPhotos= Images.Media.EXTERNAL_CONTENT_URI;
    File filepath =Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);



    private String targetFilename="img.jpg";
    //private String Camerapath ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getApplicationContext(), mLocationForPhotos.getPath(), Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);
        //Asignar los elementos del layout
        ImgPhoto = (ImageView) findViewById(R.id.ImgPhoto);
        BtnSelectImg = (Button) findViewById(R.id.BtnSelectImg);
        BtnGallery=(Button) findViewById(R.id.BtnGallery);
        BtnStudio=(Button) findViewById(R.id.BtnStudio);
        //OnClickListener para tomar la foto
        BtnSelectImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
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
        });
        //OnClickListener para abrir la galería
        BtnGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                try {  //Llamamos a la galería y esperamos que el usuario escoja una foto.
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, mLocationForPhotos);
                    startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
                }
            }
        });
        //OnClickListener para abrir nuestro estudio de edición.
        BtnStudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                try {  //Llamamos a la actividad del estudio y le pasamos la imagen.

                    ImgPhoto.buildDrawingCache();
                    Bitmap image= ImgPhoto.getDrawingCache();
                    Intent studioIntent = new Intent(getApplicationContext(),StudioActivity.class);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    stream.close();
                    studioIntent.putExtra("BMP",bytes);
                    startActivity(studioIntent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Couldn't open studio", Toast.LENGTH_LONG).show();
                }
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
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
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








}
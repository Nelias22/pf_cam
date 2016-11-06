package com.example.nayib.camprueba;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by nayib on 03/11/2016.
 */

public class UploadDialogFragment extends DialogFragment {
    ImageView imgUpload;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        imgUpload= (ImageView) getActivity().findViewById(R.id.imgUpload);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog

        String picPath= getArguments().getString("foto");
        try{
        imgUpload.setImageBitmap(BitmapFactory.decodeFile(picPath));
        }catch(Exception e){
            Toast.makeText(getActivity(), "Couldn't load picture", Toast.LENGTH_SHORT).show();
        }

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.upload_dialog, null))
                // Add action buttons

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UploadDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

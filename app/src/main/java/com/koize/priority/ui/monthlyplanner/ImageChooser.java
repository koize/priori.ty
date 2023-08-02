package com.koize.priority.ui.monthlyplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;

import com.google.android.material.chip.Chip;
import com.koize.priority.R;
import com.koize.priority.ui.category.CategoryData;
import com.koize.priority.ui.category.CategoryPopUp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageChooser extends AppCompatActivity {
    Chip cameraChip;
    ImageButton galleryChip;
    Chip deleteChip;
    Chip cancelChip;
    Chip saveChip;
    private static final int PICK_IMAGE_REQUEST_CODE = 1001;
    private static final int pic_id = 123;
    Uri image;



    public interface InterfaceCallBack {
        void sendCategory( );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chooser);
        cameraChip = findViewById(R.id.button_image_chooser_camera);
        galleryChip = findViewById(R.id.imagebutton_image_chooser);
        deleteChip = findViewById(R.id.button_image_chooser_delete);
        cancelChip = findViewById(R.id.button_image_chooser_cancel);
        saveChip = findViewById(R.id.button_image_chooser_save);

        cancelChip.setOnClickListener(v -> {
            finish();
        });

        cameraChip.setOnClickListener(v -> {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(camera_intent, pic_id);
        });

        galleryChip.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        });

        deleteChip.setOnClickListener(v -> {
            galleryChip.setImageURI(null);
        });

        saveChip.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.putExtra("image", image);
            setResult(RESULT_OK, intent);
            finish();
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Setting image on image view using Bitmap

            galleryChip.setImageURI(selectedImageUri);
            getContentResolver().takePersistableUriPermission(selectedImageUri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
            image = selectedImageUri;
        }
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            galleryChip.setImageURI(getImageUri(getApplicationContext(), photo));
            image = getImageUri(getApplicationContext(), photo);
        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
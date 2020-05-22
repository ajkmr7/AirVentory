package com.example.airventory;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class qr_generate extends AppCompatActivity {

    private QRGEncoder qrgEncoder;
    private Bitmap bitmapResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);

        TextView textViewLogo=findViewById(R.id.textViewLogo);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/astron-boy.regular.ttf");


        textViewLogo.setTypeface(custom_font1);

        Intent intent = getIntent();

        String label = intent.getStringExtra("label");

        label=label.toUpperCase();

        TextView textViewLabel=findViewById(R.id.textViewLabel);
        Log.d("HHH",label);
        textViewLabel.setText(label);


        final ImageView qr_image=findViewById(R.id.qr_image);

        final String finalLabel = label;
        if (finalLabel.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            qrgEncoder = new QRGEncoder(
                    finalLabel, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmapResult = qrgEncoder.encodeAsBitmap();
                qr_image.setImageBitmap(bitmapResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

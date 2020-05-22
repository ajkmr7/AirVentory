package com.example.airventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseFirestore db;

    public String Meridiem(){
        int n =Calendar.getInstance().get(Calendar.AM_PM);

        if(n==0){
            return " am";
        }
        else {
            return " pm";
        }
    }

    public String getDay(){
        int n =Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        String Day="";

        if(n==0){
            Day= "Monday";
        }
        else if(n==1) {
            Day="Tuesday";
        }
        else if(n==2) {
            Day="Wednesday";
        }
        else if(n==3) {
            Day="Thursday";
        }
        else if(n==4) {
            Day="Friday";
        }
        else if(n==5) {
            Day="Saturday";
        }
        else if(n==6) {
            Day="Sunday";
        }

        return Day;
    }

    public String getMinute(){

        int minute= Calendar.getInstance().get(Calendar.MINUTE);
        String minutes;

        if(minute <= 9){
            minutes="0";
            minutes+=String.valueOf(minute);
        }
        else{
            minutes=String.valueOf(minute);
        }

        return minutes;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/astron-boy.regular.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        TextView textViewLogo=findViewById(R.id.textViewLogo);
        TextView textViewDate=findViewById(R.id.textViewDate);
        TextView textViewTime=findViewById(R.id.textViewTime);
        TextView textViewRecents=findViewById(R.id.textViewRecents);

        textViewLogo.setTypeface(custom_font1);
        textViewDate.setTypeface(custom_font2);
        textViewTime.setTypeface(custom_font2);
        textViewRecents.setTypeface(custom_font2);

        Calendar cal=Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());

        textViewDate.setText(month_name+" "+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR));

        textViewTime.setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+getMinute()+" "+Meridiem());

        ImageView imageViewAdd=findViewById(R.id.imageViewAdd);

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),equipment_specifications.class));
            }
        });


    }
}

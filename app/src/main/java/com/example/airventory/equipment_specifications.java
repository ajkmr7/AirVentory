package com.example.airventory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.logging.Level.parse;

public class equipment_specifications extends AppCompatActivity {

    private ImageView imageViewChooseFile;
    private TextView textViewFile;
    public String selectedtext;
    MediaPlayer mediaPlayer;
    public String dateOfInstallation;
    private Uri filePath;
    boolean shelved;
    public String label;
    public String servicedue;
    private String text;
    String LastScan;
    boolean isAssigned;
    private RadioGroup radioGroup;
    private int flag,count;
    private EditText editTextSerialNo,editTextOrigin,editTextMfgDate,editTextInformation,editTextEquipmentName,editTextAircraftName;
    private final int PICK_IMAGE_REQUEST = 71;
    private String origin,SerialNo,MfgDate,Information,EquipmentName,AircraftName,file;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseFirestore db;

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            String file=filePath.toString();
            File myFile = new File(file);
            String displayName = null;

            if (file.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = equipment_specifications.this.getContentResolver().query(filePath, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (file.startsWith("file://")) {
                displayName = myFile.getName();
            }
            textViewFile.setText(displayName+"\nchosen.");
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+label.toUpperCase()+".jpg");
            Log.d("FBS",ref.getName()+"+++\n"+ref.getPath());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(equipment_specifications.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public String evaluate(){

        label =" ";

        label=label.concat(EquipmentName.substring(0,2));

        if(selectedtext.matches("Aircraft")){
            label=label.concat("X");
        }
        else if(selectedtext.matches("Airport")){
            label=label.concat("Y");
        }

        label=label.concat(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2,4));

        label=label.concat(origin);
        label=label.concat(SerialNo.substring(9,12));
        Log.i("label", label);

        return label;

    }

    public String Meridiem(){
        int n =Calendar.getInstance().get(Calendar.AM_PM);

        if(n==0){
            return " am";
        }
        else {
            return " pm";
        }
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


    private void get(){
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());
        LastScan=null;
        isAssigned=false;
        servicedue=null;
        shelved=false;
        origin = editTextOrigin.getText().toString();
        dateOfInstallation=month_name+ " " +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR)+" at "+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+getMinute()+" "+Meridiem().toUpperCase()+" UTC +5:30";
        SerialNo = editTextSerialNo.getText().toString();
        MfgDate = editTextMfgDate.getText().toString();
        Information = editTextInformation.getText().toString();
        EquipmentName = editTextEquipmentName.getText().toString();
        AircraftName=editTextAircraftName.getText().toString();
        file=textViewFile.getText().toString();

    }


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    public void show_Notification(){


        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(label.toUpperCase()+" was successfully added!")
                .setContentTitle("Equipment Added")
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.notification);
        mediaPlayer.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_specifications);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        Log.d("DB",db.toString());

        flag=0;

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/astron-boy.regular.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        TextView textViewLogo=findViewById(R.id.textViewLogo);

        textViewLogo.setTypeface(custom_font1);

        editTextSerialNo=findViewById(R.id.editTextSerialNo);
        editTextOrigin=findViewById(R.id.editTextOrigin);
        editTextMfgDate=findViewById(R.id.editTextMfgDate);
        editTextInformation=findViewById(R.id.editTextInformation);
        editTextEquipmentName=findViewById(R.id.editTextEquipmentName);
        editTextAircraftName=findViewById(R.id.editTextAircraftName);

        imageViewChooseFile=findViewById(R.id.imageViewChooseFile);


        editTextSerialNo.setBackgroundResource(R.drawable.rounded_edittext);
        editTextOrigin.setBackgroundResource(R.drawable.rounded_edittext);
        editTextMfgDate.setBackgroundResource(R.drawable.rounded_edittext);
        editTextAircraftName.setBackgroundResource(R.drawable.rounded_edittext);
        editTextEquipmentName.setBackgroundResource(R.drawable.rounded_edittext);
        editTextInformation.setBackgroundResource(R.drawable.rounded_edittext);

        editTextOrigin.setTypeface(custom_font2);
        editTextSerialNo.setTypeface(custom_font2);
        editTextMfgDate.setTypeface(custom_font2);
        editTextEquipmentName.setTypeface(custom_font2);
        editTextAircraftName.setTypeface(custom_font2);
        editTextInformation.setTypeface(custom_font2);


        textViewFile=findViewById(R.id.textViewFile);
        radioGroup = findViewById(R.id.radioGroup);

        get();




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                int id = radioGroup.getCheckedRadioButtonId();
                if(id!=-1){
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton =
                            radioGroup.findViewById(radioButtonID);

                    selectedtext = (String) radioButton.getText();



                    if(selectedtext.matches("Aircraft")){
                        editTextEquipmentName.setVisibility(View.VISIBLE);
                        editTextAircraftName.setVisibility(View.VISIBLE);
                        editTextInformation.setVisibility(View.VISIBLE);

                    }
                    if(selectedtext.matches("Airport")){
                        editTextEquipmentName.setVisibility(View.VISIBLE);
                        editTextInformation.setVisibility(View.VISIBLE);
                        editTextAircraftName.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new  Intent(getApplicationContext(),MainActivity.class));
            }
        });

        imageViewChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });



        ImageView imageViewNext=findViewById(R.id.imageViewNext);


        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                get();

                count=0;

                if (origin.matches("")) {
                    editTextOrigin.setError("Origin Required!");
                    flag=1;
                }
                else{
                    editTextOrigin.setError(null);
                    count++;
                }
                if (SerialNo.matches("")) {
                    editTextSerialNo.setError("Model No. Required!");
                    flag=1;
                }
                else{
                    editTextSerialNo.setError(null);
                    count++;
                }

                if (MfgDate.matches("")) {
                    editTextMfgDate.setError("Mfg. Date Required!");
                    flag=1;
                }
                else{
                    editTextMfgDate.setError(null);
                    count++;
                }
                if (Information.matches("")) {
                    editTextInformation.setError("Info. Required!");
                    flag=1;
                }
                else{
                    editTextInformation.setError(null);
                    count++;
                }
                if (EquipmentName.matches("")) {
                    editTextEquipmentName.setError("Equipment Name Required!");
                    flag=1;
                }
                else{
                    editTextEquipmentName.setError(null);
                    count++;
                }
                if(file.matches("")){
                    textViewFile.setError("");
                    flag=1;
                }
                else{
                    textViewFile.setError(null);
                    count++;
                }
                if(selectedtext.matches("Aircraft")&&(AircraftName.matches(""))){
                    editTextAircraftName.setError("Aircraft Name Required!");
                    flag=1;
                }
                else{
                    editTextAircraftName.setError(null);
                }
                if((flag==0) || (count==9)) {
                    text=evaluate();
                    Log.d("checked","inf");
                    Log.d("Label",text);
                    Map<String, Object> equipment = new HashMap<>();
                    equipment.put("isAssigned",isAssigned);
                    equipment.put("Last Scan",LastScan);
                    equipment.put("Org port", origin);
                    equipment.put("dateOfInstallation",dateOfInstallation);
                    equipment.put("modelNumber", SerialNo);
                    equipment.put("qrNumber",label.toUpperCase());
                    equipment.put("servicedue",MfgDate);
                    equipment.put("shelved",shelved);

                    Log.d("SOU","sss");
                    if( selectedtext.matches("Airport")) {
                        equipment.put("name",EquipmentName);
                        db.collection("equipmentData").document(label.toUpperCase()).set(equipment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Log.d("check","SUcess");
                                else
                                    Log.d("check",task.getException().toString());
                            }
                        });
                    }
                    if (selectedtext.matches("Aircraft")) {
                        equipment.put("name",EquipmentName);
                        db.collection("equipmentData").document(label.toUpperCase()).set(equipment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Log.d("SOU","SUcess");
                                else
                                    Log.d("SOU",task.getException().toString());
                            }
                        });
                    }

                    uploadImage();

                    show_Notification();

                    finish();
                    Intent intent = new Intent(getApplicationContext(), qr_generate.class);
                    intent.putExtra("label", text);
                    startActivity(intent);
                }
            }
        });
    }
}

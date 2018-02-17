package com.example.tanishqsaluja.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tanishqsaluja on 11/2/18.
 */

public class SettingsActivity extends AppCompatActivity {
    TextView displayName, displayEmail, displayStatus;
    Button thumb, change_status;
    CircleImageView circleImageView;
    ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    String uid;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_settings);
        displayName = findViewById(R.id.name);
        displayEmail = findViewById(R.id.email);
        displayStatus = findViewById(R.id.status);
        thumb = findViewById(R.id.changephoto);
        change_status = findViewById(R.id.changestatus);
        circleImageView = findViewById(R.id.profile_image);
        progressDialog=new ProgressDialog(this);
        //getting reference for the storage
        mStorageRef = FirebaseStorage.getInstance().getReference();


        //First grab that user frm database
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        //You can retrieve data using addEventListener or addChildeventlistener
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
            //    String photo = dataSnapshot.child("photo").getValue(String.class);


                //For the first time when we run the other data loads
                //So we want to use the offline feature of firebase HERE

                displayName.setText(name);
                displayEmail.setText(email);
                displayStatus.setText(status);
                try {
                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.bot).into(circleImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //circleImageView.setImageResource();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MyStatusAct.class);
                intent.putExtra("earlier_status", displayStatus.getText().toString());
                startActivity(intent);
            }
        });
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*      Intent gallaryIntent = new Intent();
                gallaryIntent.setType("image*s");
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallaryIntent, "Select Title"), 123);
*/
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
     /*   if(requestCode==123 && resultCode==RESULT_OK){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(SettingsActivity.this);

        }
     */   if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Uploading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                StorageReference storageReference = mStorageRef.child("profile_images").child(uid+".jpg");
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url=task.getResult().getDownloadUrl().toString();
                            databaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //to retrieve put listeners
                                    progressDialog.dismiss();
                                }
                            });
                            Toast.makeText(SettingsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Error in Uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

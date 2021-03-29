package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.transition.ChangeImageTransform;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import id.zelory.compressor.Compressor;

public class SettingProfile extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;
    private StorageReference imageStorage;
    private TextView nameS, statusS;
    private ImageView imageView;
    private int GALLARY_RRQ_CODE = 1;
    private ProgressDialog progressDialog;

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);


        nameS = findViewById(R.id.setting_name);
        statusS = findViewById(R.id.setting_status);
        imageView = findViewById(R.id.setting_image);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userId = firebaseUser.getUid();

        imageStorage = FirebaseStorage.getInstance().getReference();


        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        // saving the data and show offline..
        databaseRef.keepSynced(true);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Toast.makeText(SettingProfile.this , snapshot+"", Toast.LENGTH_LONG).show();

                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                String thumb_image = Objects.requireNonNull(snapshot.child("thumb_image").getValue()).toString();


                //
                nameS.setText(name);
                statusS.setText(status);
                // if there is no image then it shows the default image in profile...
                if (!image.equals("default")) {

                    //placeholder shows the default picture atleast the orginal picture load...

                    //Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile2).into(imageView);

                    // showing picture when network is not connect with device...
                    Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile2).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            // if any error occure or picture not saved then it shows default picture
                            Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile2).into(imageView);
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void onChangeStatus(View view) {
        String oldStatus = statusS.getText().toString();
        Intent intent = new Intent(SettingProfile.this, ChangeStatusActivity.class);
        intent.putExtra("old_status", oldStatus);
        startActivity(intent);
    }

    // getting image from gallery...
    public void onChangeImage(View view) {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLARY_RRQ_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // after getting image from gallery it convert to uri
        if (requestCode == GALLARY_RRQ_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            // selected value for croping...
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
//            Toast.makeText(SettingProfile.this , imageUri+"" , Toast.LENGTH_LONG).show();

        }

        // checking image is cropted
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            // and saving the value or cropted image in varaible...
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingProfile.this);
                progressDialog.setTitle("Uploading profile iamge...");
                progressDialog.setMessage("Please wait while we save changes.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                // then converting the reult value to uri... for saving the cropted value in database....
                Uri resultUri = result.getUri();

                //geting the file from uri..
                File thumb_file = new File(resultUri.getPath());

                String currentUserId = firebaseUser.getUid();

                byte[] thumb_bytes = new byte[0];
                try {
                    //compressing the image size...
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    thumb_bytes = bytes;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference filepath = imageStorage.child("profile_images").child(currentUserId + ".jpg");
                StorageReference thumb_filepath = imageStorage.child("profile_images").child("thumbs").child(currentUserId + ".jpg");

                // storing image in firebase storage...
                byte[] finalThumb_bytes = thumb_bytes;


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            // stroing the image link in real database
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dowload_uri = uri.toString();
                                    Log.d("DOWLOAD_URI", "" + uri.toString());


                                    UploadTask uploadTask = thumb_filepath.putBytes(finalThumb_bytes);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    String thumb_dowloadUri = uri.toString();

                                                    if (task.isSuccessful()) {

                                                        Map update_hasmap = new HashMap();
                                                        update_hasmap.put("image", dowload_uri);
                                                        update_hasmap.put("thumb_image", thumb_dowloadUri);

                                                        databaseRef.updateChildren(update_hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(SettingProfile.this, "Image Uploaded...", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingProfile.this, "Some error in uploading thumbnail", Toast.LENGTH_LONG).show();
                                                    }


                                                }
                                            });


                                        }
                                    });

                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingProfile.this, "Some error during image uploated", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

}
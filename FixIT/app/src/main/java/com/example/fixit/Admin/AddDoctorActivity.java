package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityAddDoctorBinding;
import com.example.fixit.databinding.ActivityAddMedicineBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class AddDoctorActivity extends AppCompatActivity {
    ImageView imageView;
    Uri uri;
    public FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    String name, location, specialization, contact;

    ActivityAddDoctorBinding activityAddDoctorBinding;
    Button buttonaddDoctor;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private static final int DOCTOR_ITEM_IMAGE_CODE = 440;
    FirebaseUser firebaseUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddDoctorBinding = ActivityAddDoctorBinding.inflate(getLayoutInflater());
        setContentView(activityAddDoctorBinding.getRoot());


        initViews(activityAddDoctorBinding);

        firebaseUser =firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();}// startActivity(new Intent(getApplicationContext(), LoginActivity.class));


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDoctorImage();
            }
        });
        buttonaddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFields()) {
                    return;
                } else {
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (progressBar.getParent() != null) {
                        ((ViewGroup) progressBar.getParent()).removeView(progressBar);
                    }
                    linearLayout.addView(progressBar, layoutParams);

                    uploadDoctorInfo();
                }
            }
        });
    }

    private void initViews(ActivityAddDoctorBinding activityAddDoctorBinding) {
        name = activityAddDoctorBinding.nameEditText.getText().toString();
        location = activityAddDoctorBinding.nameEditLocation.getText().toString();
        specialization = activityAddDoctorBinding.nameEditSpecialization.getText().toString();
        imageView = activityAddDoctorBinding.doctorImageView;
        buttonaddDoctor = activityAddDoctorBinding.addDoctorButton;
        linearLayout = activityAddDoctorBinding.linLayoutDoctor;
        contact = activityAddDoctorBinding.nameEditContact.getText().toString();
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void uploadDoctorInfo() {
        // Your other code
        name = activityAddDoctorBinding.nameEditText.getText().toString();
        location = activityAddDoctorBinding.nameEditLocation.getText().toString();
        specialization = activityAddDoctorBinding.nameEditSpecialization.getText().toString();
        imageView = activityAddDoctorBinding.doctorImageView;
        contact = activityAddDoctorBinding.nameEditContact.getText().toString();

        final String timestamp = "" + System.currentTimeMillis();

        if (uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            // Set values to the hashMap
            hashMap.put("name", name);
            hashMap.put("location", location);
            hashMap.put("specialization", specialization);
            hashMap.put("contact", contact);
            hashMap.put("did", timestamp);
            hashMap.put("timestamp", timestamp);
            hashMap.put("uid", firebaseAuth.getUid());//use me for shop name logic
            hashMap.put("image", "");

            DocumentReference userRef = firestore.collection("users").document(uid);
            userRef.collection("Doctor").document(timestamp).set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Doctor Added...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // progressDialog.dismiss();=====
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Your other code

            StorageReference filepath = storageReference.child("imagePost").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            // Set values to the hashMap
                            hashMap.put("name", name);
                            hashMap.put("location", location);
                            hashMap.put("specialization", specialization);
                            hashMap.put("contact", contact);
                            hashMap.put("did", timestamp);
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("uid", firebaseAuth.getUid());//use me for shop name logic
                            hashMap.put("image", "" + task.getResult().toString());


                            DocumentReference userRef = firestore.collection("users").document(uid);
                            userRef.collection("Doctor").document(timestamp).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //  progressDialog.dismiss();
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Doctor Added...", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //   progressDialog.dismiss();
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == DOCTOR_ITEM_IMAGE_CODE && data.getData() != null) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }


    public boolean validateFields() {
        name = activityAddDoctorBinding.nameEditText.getText().toString();
        location = activityAddDoctorBinding.nameEditLocation.getText().toString();
        specialization = activityAddDoctorBinding.nameEditSpecialization.getText().toString();

        if (TextUtils.isEmpty(name)) {
            activityAddDoctorBinding.nameEditText.setError("Please Insert Doctor Name");
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            activityAddDoctorBinding.nameEditLocation.setError("Please Insert Doctor's location");
            return false;
        }
        if (TextUtils.isEmpty(specialization)) {
            activityAddDoctorBinding.nameEditSpecialization.setError("Please Insert Doctor's Specialization");
            return false;
        }

        return true;
    }


    private void pickDoctorImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Doctor Image"), DOCTOR_ITEM_IMAGE_CODE);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
        startActivity(intent);
        // super.onBackPressed();
        // Finish the current activity
        finish();
    }
}
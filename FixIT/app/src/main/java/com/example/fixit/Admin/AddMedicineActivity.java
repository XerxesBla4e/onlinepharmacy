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

public class AddMedicineActivity extends AppCompatActivity {

    ImageView imageView;
    Uri uri;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    String name, category, price;
    ActivityAddMedicineBinding addMedicineBinding;
    Button buttonaddMedicine;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private static final int MEDICINE_ITEM_IMAGE_CODE = 440;
    FirebaseUser firebaseUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addMedicineBinding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(addMedicineBinding.getRoot());

        initViews(addMedicineBinding);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            startActivity(new Intent(AddMedicineActivity.this, LoginActivity.class));
            finish();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedicineImage();
            }
        });

        buttonaddMedicine.setOnClickListener(new View.OnClickListener() {
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

                    uploadMedicine();
                }
            }
        });
    }


    private void pickMedicineImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Medicine Image"), MEDICINE_ITEM_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MEDICINE_ITEM_IMAGE_CODE) {
            assert data != null;
            if (data.getData() != null) {
                uri = data.getData();
                imageView.setImageURI(uri);
            }
        }
    }

    private void initViews(ActivityAddMedicineBinding addMedicineBinding) {
        name = addMedicineBinding.mnameEditText.getText().toString();
        category = addMedicineBinding.nameEditCategory.getText().toString();
        price = addMedicineBinding.nameEditPrice.getText().toString();
        imageView = addMedicineBinding.medicineImageView;
        buttonaddMedicine = addMedicineBinding.addMedicineButton;
        linearLayout = addMedicineBinding.linLayoutMedicine;
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public boolean validateFields() {
        name = addMedicineBinding.mnameEditText.getText().toString();
        category = addMedicineBinding.nameEditCategory.getText().toString();
        price = addMedicineBinding.nameEditPrice.getText().toString();

        if (TextUtils.isEmpty(name)) {
            addMedicineBinding.mnameEditText.setError("Please insert medicine name");
            return false;
        }
        if (TextUtils.isEmpty(category)) {
            addMedicineBinding.nameEditCategory.setError("Please insert medicine category");
            return false;
        }
        if (TextUtils.isEmpty(price)) {
            addMedicineBinding.nameEditPrice.setError("Please insert medicine price");
            return false;
        }

        return true;
    }

    private void uploadMedicine() {
        final String timestamp = "" + System.currentTimeMillis();
        name = addMedicineBinding.mnameEditText.getText().toString();
        category = addMedicineBinding.nameEditCategory.getText().toString();
        price = addMedicineBinding.nameEditPrice.getText().toString();
        imageView = addMedicineBinding.medicineImageView;

        if (uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("mname", name);
            hashMap.put("mcategory", category);
            hashMap.put("mprice", price);
            hashMap.put("mid", timestamp);
            hashMap.put("mtimestamp", timestamp);
            hashMap.put("muid", firebaseAuth.getUid());
            hashMap.put("mimage", "");

            DocumentReference userRef = firestore.collection("users").document(uid);
            userRef.collection("Medicine").document(timestamp).set(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Medicine Added...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            StorageReference filepath = storageReference.child("imagePost").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("mname", name);
                            hashMap.put("mcategory", category);
                            hashMap.put("mprice", price);
                            hashMap.put("mid", timestamp);
                            hashMap.put("mtimestamp", timestamp);
                            hashMap.put("muid", firebaseAuth.getUid());
                            hashMap.put("mimage", "" + task.getResult().toString());

                            DocumentReference userRef = firestore.collection("users").document(uid);
                            userRef.collection("Medicine").document(timestamp).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Medicine Added...", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
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
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
        startActivity(intent);
        finish();
    }
}

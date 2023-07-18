package com.example.fixit.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixit.Admin.AdminMainActivity;
import com.example.fixit.Model.Users;
import com.example.fixit.Patient.PatientMainActivity;
import com.example.fixit.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding activityLoginBinding;
    String email, password;
    Button btnlogin;
    private FirebaseAuth mAuth;
    private static final String TAG = "LOGIN";

    private FirebaseUser user;
    FirebaseFirestore db;
    DocumentReference userRef;
 //   ProgressBar progressBar;
    LinearLayout linearLayout;
    FirebaseUser firebaseUser;
    String uid;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        initViews();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = activityLoginBinding.etEmaill.getText().toString();
                password = activityLoginBinding.etPasswordl.getText().toString();

                if (!validateFields()) {
                    // Handle validation errors
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid = mAuth.getCurrentUser().getUid();
                                makeOnline(uid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                           //     progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Rest of your code...

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PatientSignup.class));
            }
        });
    }

    private void initViews() {
        btnlogin = activityLoginBinding.btnlogin;
        signup = activityLoginBinding.textsignup;
        linearLayout = activityLoginBinding.linLayoutLogin;
     //   progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
    }

    private boolean validateFields() {
        String noSpecialChars = "\\A[A-Za-z]{4,20}\\z";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String phoneRegex = "^[+]?[0-9]{10,15}$";
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if (TextUtils.isEmpty(email)) {
            activityLoginBinding.etEmaill.setError("Email field can't be empty");
            return false;
        } else if (!email.matches(emailRegex)) {
            activityLoginBinding.etEmaill.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            activityLoginBinding.etPasswordl.setError("Password field can't be empty");
            return false;
        } else if (!password.matches(passwordRegex)) {
            activityLoginBinding.etPasswordl.setError("Password must contain at least 8 characters including one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        }

        return true;
    }

    private void makeOnline(String uid) {
        userRef = db.collection("users").document(uid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        userRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkUserType(uid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    //    progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              //  progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        Users userProfile = snapshot.toObject(Users.class);
                        if (userProfile != null) {
                            String accountType = userProfile.getAccounttype();

                            if (accountType.equals("Patient")) {
                                startActivity(new Intent(getApplicationContext(), PatientMainActivity.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
                            }
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

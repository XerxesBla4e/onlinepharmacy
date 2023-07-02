package com.example.fixit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixit.Admin.AdminMainActivity;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.Model.Users;
import com.example.fixit.Patient.PatientMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;
    TextView appname;
    private static final String TAG = "Splashscreen";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        //appname = findViewById(R.id.appnme);
        // lottie = findViewById(R.id.lottie);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //   appname.animate().translationY(-1400).setDuration(2700).setStartDelay(0);
        //  lottie.animate().translationX(2000).setDuration(2000).setStartDelay(2900);

        if (!isConnected()) {
            showNoInternetDialog();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    } else {
                        checkUserType();
                    }
                }
            }, SPLASH_TIMER);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to the internet to use this app. Some features may not be available without an internet connection.");
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void checkUserType() {
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userRef = firestore.collection("users").document(user.getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.exists()) {
                            Users userProfile = snapshot.toObject(Users.class);
                            if (userProfile != null) {
                                String accountType = userProfile.getAccounttype();

                                if (accountType.equals("Patient")) {
                                    startActivity(new Intent(SplashScreen.this, PatientMainActivity.class));
                                } else {
                                    startActivity(new Intent(SplashScreen.this, AdminMainActivity.class));
                                }
                            }
                        } else {
                            // User collection or document doesn't exist
                            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                        }
                    } else {
                        Toast.makeText(SplashScreen.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        // Go back to LoginActivity
                      startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    }
                }
            });
        } else {
             startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }

    }
}

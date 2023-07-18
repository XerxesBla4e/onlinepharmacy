package com.example.fixit.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixit.R;
import com.example.fixit.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    DatePicker datePicker;
    String date, name, age, email, phonenumber, location, password, repass, gender;
    String district, city, state, country, address;
    TextView adminSignup;
    Button buttonSubmit, buttonPickLocale;
    Spinner spinner;
    Double latitude, longitude;

    ProgressBar progressBar;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    LinearLayout linearLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationManager locationManager;
    LocationListener locationListener;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static final int MY_PERMISSION_REQUEST_CODE = 71;
    private static final int PERMISSION_REQUEST_CODE = 444;

    private static final String TAG = "dd";
    ActivityUpdateProfileBinding activityUpdateProfileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityUpdateProfileBinding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(activityUpdateProfileBinding.getRoot());

        initViews(activityUpdateProfileBinding);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        datePicker = activityUpdateProfileBinding.datePicker;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, i);
                    selectedDate.set(Calendar.MONTH, i1);
                    selectedDate.set(Calendar.DAY_OF_MONTH, i2);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    date = sdf.format(selectedDate.getTime());
                }
            });
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFields()) {
                    return;
                } else {
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout.addView(progressBar, layoutParams);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    uploadData();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
                                    // Handle the failure scenario if necessary
                                }
                            });
                }
            }
        });

        buttonPickLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLocation();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }
    }

    private void initViews(ActivityUpdateProfileBinding activityPatientSignupBinding) {
        progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
        spinner = activityPatientSignupBinding.spinnerGender;
        buttonSubmit = activityPatientSignupBinding.btnSubmit;
        buttonPickLocale = activityPatientSignupBinding.btnLocation;
        linearLayout = activityPatientSignupBinding.PatientLinLayout;
        adminSignup = activityPatientSignupBinding.adminSignup;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setInterval(UPDATE_INTERVAL);

    }

    private void uploadData() {
        String userId = mAuth.getCurrentUser().getUid();
        final String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, Object> user = new HashMap<>();
        user.put("uid", userId);
        user.put("name", name);
        user.put("age", age);
        user.put("email", email);
        user.put("phonenumber", phonenumber);
        user.put("location", location);
        user.put("city", "" + city);
        user.put("state", "" + state);
        user.put("country", "" + country);
        user.put("district", "" + district);
        user.put("gender", gender);
        user.put("DOB", date);
        user.put("timestamp", timestamp);
        user.put("latitude", "" + longitude);
        user.put("longitude", "" + latitude);
        user.put("accounttype", "Patient");
        user.put("online", "true");

        db.collection("users")
                .document(userId)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Account updated successfully", Toast.LENGTH_SHORT).show();
                        Intent x6 = new Intent(getApplicationContext(), LoginActivity.class);
                        x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(x6);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error while updating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean validateFields() {
        String noSpecialChars = "\\A[A-Za-z]{4,20}\\z";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String phoneRegex = "^[+]?[0-9]{10,15}$";
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        int minAge = 10; // Minimum age requirement
        int maxAge = 100; // Maximum age limit

        name = activityUpdateProfileBinding.etName.getText().toString();
        age = activityUpdateProfileBinding.etAge.getText().toString();
        email = activityUpdateProfileBinding.etEmail.getText().toString();
        phonenumber = activityUpdateProfileBinding.etPhoneNumber.getText().toString();
        location = activityUpdateProfileBinding.etLocation.getText().toString();
        password = activityUpdateProfileBinding.etPassword.getText().toString();
        repass = activityUpdateProfileBinding.etRePassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            activityUpdateProfileBinding.etName.setError("Username field can't be empty");
            return false;
        } else if (!name.matches(noSpecialChars)) {
            activityUpdateProfileBinding.etName.setError("Only letters are allowed!");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            activityUpdateProfileBinding.etEmail.setError("Email field can't be empty");
            return false;
        } else if (!email.matches(emailRegex)) {
            activityUpdateProfileBinding.etEmail.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(phonenumber)) {
            activityUpdateProfileBinding.etPhoneNumber.setError("Phone number field can't be empty");
            return false;
        } else if (!phonenumber.matches(phoneRegex)) {
            activityUpdateProfileBinding.etPhoneNumber.setError("Invalid phone number format");
            return false;
        }

        if (TextUtils.isEmpty(age)) {
            activityUpdateProfileBinding.etAge.setError("Age field can't be empty");
            return false;
        }
        int ageValue = Integer.parseInt(age);
        if (ageValue < minAge || ageValue > maxAge) {
            activityUpdateProfileBinding.etAge.setError("Invalid age. Must be between 10 and 100.");
            return false;
        }

        if (TextUtils.isEmpty(location)) {
            activityUpdateProfileBinding.etLocation.setError("Location field can't be empty");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            activityUpdateProfileBinding.etPassword.setError("Password field can't be empty");
            return false;
        } else if (!password.matches(passwordRegex)) {
            activityUpdateProfileBinding.etPassword.setError("Password must contain at least 8 characters including one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        }

        if (TextUtils.isEmpty(repass)) {
            activityUpdateProfileBinding.etRePassword.setError("Confirm Password field can't be empty");
            return false;
        } else if (!repass.equals(password)) {
            activityUpdateProfileBinding.etRePassword.setError("Passwords do not match");
            return false;
        }
        if (TextUtils.isEmpty(date)) {
            activityUpdateProfileBinding.tvDateHint.setError("Please select a date");
            return false;
        }

        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(getApplicationContext(), "Select Appropriate Gender Please", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startLocationUpdates();
        } else {
            startLegacyLocationUpdates();
            //  Toast.makeText(getApplicationContext(), "Device Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    getLocationAddress(longitude, latitude);

                    //  Toast.makeText(getApplicationContext(), "Address " + longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates again
                startLegacyLocationUpdates();
            } else {
                // Permission denied, handle it accordingly (e.g., show a message to the user)
                Toast.makeText(getApplicationContext(), "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void startLegacyLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    getLocationAddress(longitude, latitude);
                }
            };

            // Request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Request the missing permissions from the user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(getApplicationContext(), "Location Services Are Disabled\nPlease enable GPS or " +
                    "network provider", Toast.LENGTH_LONG).show();
        }
    }

    private void getLocationAddress(Double longitude, Double latitude) {
        try {
            Geocoder geocoder = new Geocoder(UpdateProfile.this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            address = addressList.get(0).getAddressLine(0);

        //    Toast.makeText(getApplicationContext(), "Address: " + address, Toast.LENGTH_SHORT).show();
            activityUpdateProfileBinding.etLocation.setText(address);
            city = addressList.get(0).getLocality();//city
            state = addressList.get(0).getAdminArea();//region
            country = addressList.get(0).getCountryName();//country
            district = addressList.get(0).getSubAdminArea();//district

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        // super.onBackPressed();
        // Finish the current activity
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        displayLocation();
    }
}
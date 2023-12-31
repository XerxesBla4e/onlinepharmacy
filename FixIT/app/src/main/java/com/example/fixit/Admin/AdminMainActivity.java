package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fixit.Adapter.MainOrder;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.Authentication.UpdateProfile;
import com.example.fixit.Dialogs.AddObjectDialog;
import com.example.fixit.Interface.OnMoveToPatientDets;
import com.example.fixit.Model.MedicineModel;
import com.example.fixit.Model.Orders;
import com.example.fixit.Model.UserDets;
import com.example.fixit.Patient.PatientMainActivity;
import com.example.fixit.R;
import com.example.fixit.SplashScreen;
import com.example.fixit.databinding.ActivityAdminMainBinding;
import com.example.fixit.webview.websiteActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public FloatingActionButton floatingActionButton;
    public Toolbar toolbar;
    ActivityAdminMainBinding activityAdminMainBinding;

    RecyclerView recyclerView;
    MainOrder mainOrder;
    List<Orders> ordersList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    String uid1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String TAG = "Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAdminMainBinding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(activityAdminMainBinding.getRoot());

        toolbar = activityAdminMainBinding.toolbar;
        floatingActionButton = activityAdminMainBinding.fbAddTransaction;
        bottomNavigationView = activityAdminMainBinding.bottomNavView;

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddObjectDialog addObjectDialog = new AddObjectDialog();
                addObjectDialog.show(getSupportFragmentManager(), "add object dialog");
            }
        });

        initBottomNavView();

        setSupportActionBar(toolbar);

        initViews(activityAdminMainBinding);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid1 = user.getUid();
                    retrieveOrders();
                    requestLocationUpdates();
                } else {
                    startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void retrieveOrders() {
        firestore = FirebaseFirestore.getInstance();

        mainOrder.OnMoveToPatient(new OnMoveToPatientDets() {
            @Override
            public void onMoveToDets(Orders ordersmodel) {
                Intent intent = new Intent(getApplicationContext(), PatientDetailsActivity.class);
                intent.putExtra("ordersModel", ordersmodel);
                startActivity(intent);
            }
        });

        CollectionReference ordersRef = firestore.collection("users")
                .document(uid1)
                .collection("orders");

        ordersList = new ArrayList<>();

        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String orderID = document.getString("orderID");
                            String orderTime = document.getString("orderTime");
                            String orderStatus = document.getString("orderStatus");
                            String patient = document.getString("orderBy");
                            Orders orders = new Orders(orderID, orderTime, orderStatus, patient);
                            ordersList.add(orders);
                        }
                        mainOrder.submitList(ordersList);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Log or display the error message
                    }
                }
            }
        });
    }


    private void requestLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateUserLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void updateUserLocation(double latitude, double longitude) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore.collection("users").document(uid1);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("latitude", ""+latitude);
        updateData.put("longitude", ""+longitude);

        documentRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        // Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "" + e);
                        // Handle any errors
                        // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews(ActivityAdminMainBinding activityAdminMainBinding) {
        recyclerView = activityAdminMainBinding.recyclerView;
        mainOrder = new MainOrder();
        ordersList = new ArrayList<>();
        bottomNavigationView = activityAdminMainBinding.bottomNavView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainOrder);
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_doctors) {
                    Intent x = new Intent(AdminMainActivity.this, ViewDoctorsActivity.class);
                  //  x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_medicine) {
                    Intent x6 = new Intent(AdminMainActivity.this, ViewMedicineActivity.class);
                  //  x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "In progress", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_xer) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("XerxesCodes")
                    .setMessage("Developed by XerxesCodes54")
                    .setNegativeButton("visit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent xer3 = new Intent(AdminMainActivity.this, websiteActivity.class);
                            startActivity(xer3);
                        }
                    }).setPositiveButton("Invite friends", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String message = "Hey,wsap check out this amazing app " +
                                    "\nDownload here";

                            Intent xer4 = new Intent(Intent.ACTION_SEND);
                            xer4.putExtra(Intent.EXTRA_TEXT, message);
                            xer4.setType("text/plain");
                            Intent chooserIntent = Intent.createChooser(xer4, "Invite Via..");
                            startActivity(chooserIntent);
                        }
                    });
            builder.show();

        } else if (item.getItemId() == R.id.menu_updateprof) {
            Intent xx = new Intent(AdminMainActivity.this, UpdateProfile.class);
            xx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(xx);
        } else if (item.getItemId() == R.id.menu_logout) {
            makeOffline();
            firebaseAuth.signOut();
            startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }

    private void makeOffline() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = firestore.collection("users").document(uid);

            userRef.update("online", "false")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Successfully updated the user's online status
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update the user's online status
                        }
                    });
        }
    }

}

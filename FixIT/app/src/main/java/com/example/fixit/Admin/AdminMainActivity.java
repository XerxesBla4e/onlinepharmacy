package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    //private RecyclerView transactionRecView;
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

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid1 = firebaseUser.getUid();

        //    checkUser();

        mainOrder.OnMoveToPatient(new OnMoveToPatientDets() {
            @Override
            public void onMoveToDets(Orders ordersmodel) {
                // Handle the click event, for example, navigate to doctor details screen
                Intent intent = new Intent(getApplicationContext(), PatientDetailsActivity.class);
                intent.putExtra("ordersModel", ordersmodel);
                startActivity(intent);
            }
        });
        CollectionReference ordersRef = FirebaseFirestore.getInstance().collection("users")
                .document(uid1)
                .collection("orders");

        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Handle each order document here
                            String orderID = document.getString("orderID");
                            String orderTime = document.getString("orderTime");
                            String orderStatus = document.getString("orderStatus");
                            String patient = document.getString("orderBy");
                            // Retrieve other order details as needed
                            // Create a Medicine object and add it to the list
                            Orders orders = new Orders(orderID, orderTime, orderStatus, patient);
                            ordersList.add(orders);
                        }
                        mainOrder.submitList(ordersList);
                    }
                } else {
                    // Handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Log or display the error message
                    }
                }
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
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_medicine) {
                    Intent x6 = new Intent(AdminMainActivity.this, ViewMedicineActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        return super.onOptionsItemSelected(item);
    }

    private void makeOffline() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore.collection("Users").document(uid1);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("online", "false");

        documentRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
package com.example.fixit.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fixit.Adapter.MedicineAdapter;
import com.example.fixit.Admin.AdminMainActivity;
import com.example.fixit.Admin.ViewDoctorsActivity;
import com.example.fixit.Admin.ViewMedicineActivity;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.Authentication.UpdateProfile;
import com.example.fixit.Interface.OnAddToCartClickListener;
import com.example.fixit.Model.Medicine;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.Model.UserDets;
import com.example.fixit.R;
import com.example.fixit.SplashScreen;
import com.example.fixit.ViewModel.MedicineViewModel;
import com.example.fixit.databinding.ActivityPatientMainBinding;
import com.example.fixit.webview.websiteActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

public class PatientMainActivity extends AppCompatActivity {

    private MedicineViewModel medicineViewModel;
    private BottomNavigationView bottomNavigationView;
    //private RecyclerView transactionRecView;
    private Toolbar toolbar;
    ActivityPatientMainBinding activityPatientMainBinding;
    RecyclerView recyclerView;
    MedicineAdapter medicineAdapter;
    List<Medicinecart> medicineList;
    FirebaseFirestore fireStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid1;
    SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPatientMainBinding = ActivityPatientMainBinding.inflate(getLayoutInflater());
        setContentView(activityPatientMainBinding.getRoot());

        initViews(activityPatientMainBinding);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
        }
        // checkUser();


        initBottomNavView();

        setSupportActionBar(toolbar);

        //Categories Spinner
        RetrieveCategories();

        medicineViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                .getInstance((Application) this.getApplicationContext())).get(MedicineViewModel.class);

        medicineAdapter.setOnAddToCartClickListener(new OnAddToCartClickListener() {
            @Override
            public void onAddToCartClick(Medicinecart medicine) {
                AddtoCart(medicine);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(medicineAdapter);

        // Replace "desiredAccountType" with the account type you want to filter by
        retrieveMedications();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    medicineViewModel.delete(medicineAdapter.getMed(viewHolder.getAdapterPosition()));
                    Toast.makeText(PatientMainActivity.this, "Cart Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }).attachToRecyclerView(activityPatientMainBinding.recyclerView);
    }

    private void retrieveMedications() {
        String desiredAccountType = "Admin";

        CollectionReference usersRef = fireStore.collection("users");

        usersRef.whereEqualTo("accounttype", desiredAccountType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            medicineList = new ArrayList<>(); //new list to hold the retrieved medicines

                            for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                String userId = userDocument.getId();

                                CollectionReference userMedicinesRef = usersRef.document(userId)
                                        .collection("Medicine");

                                userMedicinesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot medicineDocument : task.getResult()) {
                                                // Retrieve the medicine data from the document
                                                Medicinecart medicine = medicineDocument.toObject(Medicinecart.class);
                                                // Add the Medicinecart object to the list
                                                medicineList.add(medicine);
                                            }

                                            // Update the adapter with the retrieved medicine list
                                            medicineAdapter.submitList(medicineList);
                                        } else {
                                            Log.d("TAG", "Error getting medicines: " + task.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("TAG", "Error getting users: " + task.getException());
                        }
                    }
                });

    }

    private void RetrieveCategories() {
        List<String> categoriesList = new ArrayList<>();
        for (Medicinecart medicine : medicineList) {
            String category1 = medicine.getMCategory();
            if (!categoriesList.contains(category1)) {
                categoriesList.add(category1);
            }
        }

        Spinner categorySpinner = activityPatientMainBinding.spinnercat;
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        SearchView searchView = activityPatientMainBinding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchQuery = query.trim();
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                filterMedications(selectedCategory, searchQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.trim();
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                filterMedications(selectedCategory, searchQuery);
                return true;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterMedications(String selectedCategory, String searchQuery) {
        List<Medicinecart> filteredList = new ArrayList<>();

        // Check if search query is empty
        if (searchQuery.isEmpty()) {
            filteredList.addAll(medicineList);
        } else {
            // Apply search query filter
            for (Medicinecart medication : medicineList) {
                if (medication.getMName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredList.add(medication);
                }
            }
        }

        // Apply category filter
        if (!selectedCategory.isEmpty()) {
            List<Medicinecart> categoryFilteredList = new ArrayList<>();
            for (Medicinecart medication : filteredList) {
                if (medication.getMCategory().equals(selectedCategory)) {
                    categoryFilteredList.add(medication);
                }
            }
            filteredList = categoryFilteredList;
        }

        // Update RecyclerView adapter with filtered list
        medicineAdapter.submitList(filteredList);
        medicineAdapter.notifyDataSetChanged();
    }

    private void AddtoCart(Medicinecart medicine) {
        Medicinecart medicinecart = new Medicinecart(medicine.getMName(), medicine.getMCategory(),
                medicine.getMPrice(), medicine.getMId(), medicine.getMTimestamp(),
                medicine.getMImage(), medicine.getMUid());
        medicineViewModel.insert(medicinecart);
        Toast.makeText(PatientMainActivity.this, "Cart Item Added Successfully", Toast.LENGTH_SHORT).show();
    }

    private void initViews(ActivityPatientMainBinding activityPatientMainBinding) {
        recyclerView = activityPatientMainBinding.recyclerView;
        toolbar = activityPatientMainBinding.toolbar;
        bottomNavigationView = activityPatientMainBinding.bottomNavView;
        medicineAdapter = new MedicineAdapter();
        medicineList = new ArrayList<>();
        searchBar = activityPatientMainBinding.searchView;
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_doctors) {
                    Intent x = new Intent(PatientMainActivity.this, DoctorViewActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_cart) {
                    Intent x6 = new Intent(PatientMainActivity.this, OrdersActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "Option Not available at the moment", Toast.LENGTH_SHORT).show();
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
                            Intent xer3 = new Intent(PatientMainActivity.this, websiteActivity.class);
                            startActivity(xer3);
                        }
                    }).setPositiveButton("Invite friends", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String message = "Hey,wsap check out this amazing app " +
                                    "\nto help you manage your sente.\nDownload here";

                            Intent xer4 = new Intent(Intent.ACTION_SEND);
                            xer4.putExtra(Intent.EXTRA_TEXT, message);
                            xer4.setType("text/plain");
                            Intent chooserIntent = Intent.createChooser(xer4, "Invite Via..");
                            startActivity(chooserIntent);
                        }
                    });
            builder.show();

        } else if (item.getItemId() == R.id.menu_updateprof) {
            Intent xx = new Intent(PatientMainActivity.this, UpdateProfile.class);
            xx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(xx);
        } else if (item.getItemId() == R.id.menu_logout) {
            makeOffline();
            firebaseAuth.signOut();
            startActivity(new Intent(PatientMainActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeOffline() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore.collection("users").document(firebaseAuth.getUid());

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
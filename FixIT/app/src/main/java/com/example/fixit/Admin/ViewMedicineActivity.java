package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fixit.Adapter.MedicineA;
import com.example.fixit.Admin.ViewDoctorsActivity;
import com.example.fixit.Admin.AdminMainActivity;
import com.example.fixit.Model.Medicine;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityViewMedicineBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMedicineActivity extends AppCompatActivity {

    private ActivityViewMedicineBinding activityViewMedicineBinding;
    private RecyclerView recyclerView;
    private MedicineA medicineAdapter;
    private List<Medicine> medicineList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private String uid1;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewMedicineBinding = ActivityViewMedicineBinding.inflate(getLayoutInflater());
        setContentView(activityViewMedicineBinding.getRoot());

        initViews();
        setupRecyclerView();
        initBottomNavView();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
            retrieveMedicines();
            //Toast.makeText(getApplicationContext(), "" + uid1, Toast.LENGTH_SHORT).show();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    deleteMedicineItem(viewHolder);
                } else {
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void initViews() {
        recyclerView = activityViewMedicineBinding.recyclerView2;
        bottomNavigationView = activityViewMedicineBinding.bottomNavigationView;
    }

    private void setupRecyclerView() {
        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineA(medicineList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(medicineAdapter);
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_medicine);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_doctors) {
                    Intent x = new Intent(ViewMedicineActivity.this, ViewDoctorsActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_home) {
                    Intent x6 = new Intent(ViewMedicineActivity.this, AdminMainActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "Under development", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveMedicines() {
        CollectionReference medicineRef = firestore.collection("users")
                .document(uid1)
                .collection("Medicine");
        medicineRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Medicine medicine = documentSnapshot.toObject(Medicine.class);
                            medicineList.add(medicine);
                        }

                        if (medicineList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No medicine available", Toast.LENGTH_SHORT).show();
                        }

                        medicineAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to retrieve medicine items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void deleteMedicineItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Medicine medicine = medicineList.get(position);

            DocumentReference medicineRef = firestore.collection("users")
                    .document(uid1)
                    .collection("Medicine")
                    .document(medicine.getMId());

            medicineRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ViewMedicineActivity.this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                            medicineList.remove(position);
                            medicineAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewMedicineActivity.this, "Failed to delete medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

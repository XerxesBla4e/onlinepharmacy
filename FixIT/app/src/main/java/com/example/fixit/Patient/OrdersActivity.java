package com.example.fixit.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixit.Adapter.MedicineAdapter;
import com.example.fixit.Adapter.MedicineAdapter1;
import com.example.fixit.Admin.PatientDetailsActivity;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.FCMSend;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.Model.UserDets;
import com.example.fixit.R;
import com.example.fixit.ViewModel.MedicineViewModel;
import com.example.fixit.databinding.ActivityOrdersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding activityOrdersBinding;
    RecyclerView recyclerView;
    Button buttonOrder;
    TextView textView;
    MedicineViewModel medicineViewModel;
    MedicineAdapter1 medicineAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    private static final String TAG = "error";
    String notdoctortoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityOrdersBinding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(activityOrdersBinding.getRoot());

        initViews(activityOrdersBinding);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineAdapter = new MedicineAdapter1();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(medicineAdapter);

        medicineViewModel.getAllMeds().observe(this, new Observer<List<Medicinecart>>() {
            @Override
            public void onChanged(List<Medicinecart> medicinecarts) {
                medicineAdapter.submitList(medicinecarts);
            }
        });

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processCarOrder();
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    medicineViewModel.delete(medicineAdapter.getMed(viewHolder.getAdapterPosition()));
                    Toast.makeText(OrdersActivity.this, "Cart Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }).attachToRecyclerView(activityOrdersBinding.recyclerView);
    }

    @SuppressLint("DefaultLocale")
    private void processCarOrder() {
        List<Medicinecart> medicinecarts = medicineAdapter.getCurrentList();
        //int itemCount = medicinecarts.size();
        if (medicinecarts.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            double totalPrice = 0.0;
            for (Medicinecart medicine : medicinecarts) {
                String desiredAccountType = "Admin";
                String desiredMedicineId = medicine.getMId();
                String medicineMPrice1 = medicine.getMPrice();
                // Remove any non-numeric characters from the string
                String priceWithoutCurrency = medicineMPrice1.replaceAll("[^\\d.]", "");
                // Parse the price as a double
                double mPrice2 = Double.parseDouble(priceWithoutCurrency);
                totalPrice += mPrice2;

                CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

                usersRef.whereEqualTo("accounttype", desiredAccountType)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                            String userId = document.getId();
                                            // Check if the admin has the desired medicine
                                            CollectionReference medicineRef = FirebaseFirestore.getInstance().collection("users")
                                                    .document(userId)
                                                    .collection("Medicine");
                                            medicineRef.whereEqualTo("mid", desiredMedicineId)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                QuerySnapshot medicineQuerySnapshot = task.getResult();
                                                                if (medicineQuerySnapshot != null && !medicineQuerySnapshot.isEmpty()) {
                                                                    // The admin has the desired medicine
                                                                    // Create a new order for the admin
                                                                    RetrieveAdminToken(userId);

                                                                    // Create the order data
                                                                    final String timestamp = "" + System.currentTimeMillis();
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("orderID", "" + timestamp);
                                                                    hashMap.put("orderTime", "" + timestamp);
                                                                    hashMap.put("orderStatus", "In Progress");
                                                                    hashMap.put("orderBy", "" + firebaseAuth.getUid());
                                                                    //add orderto

                                                                    CollectionReference ordersRef = FirebaseFirestore.getInstance().collection("users")
                                                                            .document(userId)
                                                                            .collection("orders");

                                                                    ordersRef.document(timestamp).set(hashMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // Order added successfully
                                                                                    List<Medicinecart> medicinecarts = medicineAdapter.getCurrentList();
                                                                                    for (Medicinecart medicine : medicinecarts) {
                                                                                        // Check if the medicine id falls under the admin's medicines
                                                                                        if (medicine.getMId().equals(desiredMedicineId)) {
                                                                                            // Create a new order for the medicine item within the admin's order
                                                                                            CollectionReference medicineOrdersRef = ordersRef.document(timestamp)
                                                                                                    .collection("medicineOrders");

                                                                                            // Create a HashMap to store the details of the medicine
                                                                                            HashMap<String, Object> medicineDetails = new HashMap<>();
                                                                                            medicineDetails.put("mId", medicine.getMId());
                                                                                            medicineDetails.put("mName", medicine.getMName());
                                                                                            medicineDetails.put("mCategory", medicine.getMCategory());
                                                                                            medicineDetails.put("mPrice", medicine.getMPrice());
                                                                                            medicineDetails.put("mTimestamp", medicine.getMTimestamp());
                                                                                            medicineDetails.put("mUid", medicine.getMUid());
                                                                                            medicineDetails.put("mImage", medicine.getMImage());

                                                                                            medicineOrdersRef.add(medicineDetails)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                                                            // Medicine order added successfully
                                                                                                            Toast.makeText(getApplicationContext(), "Medicine Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                                                                                            medicineAdapter.clearCart();
                                                                                                            prepareNotificationMessage("New Medicine Order: ID" + timestamp);

                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            // Error adding medicine order
                                                                                                            Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Error adding order
                                                                                    Toast.makeText(getApplicationContext(), "Failed to process order " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                // Handle the error
                                                                Exception exception = task.getException();
                                                                if (exception != null) {
                                                                    Log.d(TAG, exception + "");
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    // Handle the error
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        Log.d(TAG, exception + "");
                                    }
                                }
                            }
                        });
            }
            textView.setText("TOTAL COST: UGX " + totalPrice);
        }
    }

    private void RetrieveAdminToken(String userId) {
        DocumentReference userRef = firestore.collection("users").document(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User document exists, retrieve the data
                        UserDets user = document.toObject(UserDets.class);

                        if (user.getToken() != null) {
                            notdoctortoken = user.getToken();
                        }

                    } else {
                        // User document does not exist
                        // Handle accordingly
                        Toast.makeText(getApplicationContext(), "Admin Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // An error occurred
                    Exception exception = task.getException();
                    // Handle the error
                }
            }
        });
    }

    private void initViews(ActivityOrdersBinding activityOrdersBinding) {
        recyclerView = activityOrdersBinding.recyclerView;
        buttonOrder = activityOrdersBinding.orderButton;
        textView = activityOrdersBinding.total;
        medicineAdapter = new MedicineAdapter1();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PatientMainActivity.class);
        startActivity(intent);
        // super.onBackPressed();
        // Finish the current activity
        finish();
    }

    private void prepareNotificationMessage(String message) {
        if (notdoctortoken != null) {
            FCMSend.pushNotification(
                    OrdersActivity.this,
                    notdoctortoken,
                    "New Order",
                    message
            );
        }
    }
}
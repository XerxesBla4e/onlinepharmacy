package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixit.Adapter.MedicineOrdersAdapter;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.FCMSend;
import com.example.fixit.Model.Medicine;
import com.example.fixit.Model.Medicinecart;
import com.example.fixit.Model.Orders;
import com.example.fixit.Model.UserDets;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityPatientDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientDetailsActivity extends AppCompatActivity {

    ActivityPatientDetailsBinding patientDetailsBinding;
    RecyclerView recyclerView;
    TextView patientname, location1, status1, totalprice;
    ImageView edit, delete;

    Orders ordersModel;
    List<Medicine> medicineList;
    String notpatienttoken;

    MedicineOrdersAdapter medicineOrdersAdapter;
    FirebaseFirestore firestore;
    double total = 0.0;
    FirebaseAuth firebaseAuth;
    String OrderID, OrderBy;
    FirebaseUser firebaseUser;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        patientDetailsBinding = ActivityPatientDetailsBinding.inflate(getLayoutInflater());
        setContentView(patientDetailsBinding.getRoot());

        initViews(patientDetailsBinding);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            id = firebaseUser.getUid();
        }

        RetrievePersonalDets();

        // Retrieve the intent that started the activity
        Intent intent = getIntent();

        // Check if the intent contains the extra data
        if (intent.hasExtra("ordersModel")) {
            // Extract the Orders object from the intent's extra data
            ordersModel = intent.getParcelableExtra("ordersModel");
            OrderID = ordersModel.getOrderID();
            OrderBy = ordersModel.getOrderBy();
            status1.setText(ordersModel.getOrderStatus());
        }

        // Create a Firestore query to retrieve the medicine orders for the specific order and user
        CollectionReference medicineOrdersRef = FirebaseFirestore.getInstance().collection("users")
                .document(id)
                .collection("orders")
                .document(OrderID)
                .collection("medicineOrders");

        // Perform the query
        medicineOrdersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Convert the document snapshot to a MedicineOrder object
                            Medicine medicineOrder = document.toObject(Medicine.class);
                            String medicineMPrice1 = medicineOrder.getMPrice();
                            double mPrice = Double.parseDouble(medicineMPrice1);
                            total += mPrice;
                            // Do something with the medicine object
                            medicineList.add(medicineOrder);
                        }
                        medicineOrdersAdapter.submitList(medicineList);
                        totalprice.setText(String.format("%.2f", total));
                        totalprice.requestLayout();
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


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatusDialog();
            }
        });
    }

    private void updateOrderStatusDialog() {
        final String[] status3 = {"In Progress", "Confirmed", "Cancelled"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PatientDetailsActivity.this);
        mBuilder.setTitle("Update Order Status");
        mBuilder.setSingleChoiceItems(status3, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int xer) {
                if (xer == 0) {
                    String Message = "In Progress";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.lightGreen));
                } else if (xer == 1) {
                    String Message = "Confirmed";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.teal_700));
                } else if (xer == 2) {
                    String Message = "Cancelled";
                    updateOrderStatus(Message);
                    status1.setTextColor(getBaseContext().getResources().getColor(R.color.red));
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mBuilder.show();
    }

    private void updateOrderStatus(String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the specific order document
        DocumentReference orderRef = db.collection("users")
                .document(id)
                .collection("orders")
                .document(OrderID);

        // Update the orderStatus field with the new message
        orderRef.update("orderStatus", message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Order is now " + message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void prepareNotificationMessage(String message) {
        if (notpatienttoken != null) {
            FCMSend.pushNotification(
                    PatientDetailsActivity.this,
                    notpatienttoken,
                    "Status Update",
                    message
            );
        }
    }

    private void RetrievePersonalDets() {
        if (OrderBy != null) {
            DocumentReference userRef = firestore.collection("users").document(OrderBy);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User document exists, retrieve the data
                            UserDets user = document.toObject(UserDets.class);

                            String location = user.getLocation();
                            location1.setText(location);
                            notpatienttoken = user.getToken();

                        } else {
                            // User document does not exist
                            // Handle accordingly
                            Toast.makeText(getApplicationContext(), "Patient Doesn't Have Personal Info", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // An error occurred
                        Exception exception = task.getException();
                        // Handle the error
                    }
                }
            });
        } else {
            //Toast.makeText(getApplicationContext(), "Order Doesn't Exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(ActivityPatientDetailsBinding patientDetailsBinding) {
        recyclerView = patientDetailsBinding.recyclerView;
        patientname = patientDetailsBinding.patientname;
        location1 = patientDetailsBinding.patientlocation;
        status1 = patientDetailsBinding.orderStatus;
        totalprice = patientDetailsBinding.totalprice;
        edit = patientDetailsBinding.editstatus;
        delete = patientDetailsBinding.deleteorder;
        medicineOrdersAdapter = new MedicineOrdersAdapter();
        medicineList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(medicineOrdersAdapter);
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

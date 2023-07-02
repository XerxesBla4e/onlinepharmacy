package com.example.fixit.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.fixit.DAO.MedicineDAO;
import com.example.fixit.Database.MedicineDatabase;
import com.example.fixit.Model.Medicinecart;

import java.util.List;

public class MedicineRepo {
    private MedicineDAO medicineDAO;
    private LiveData<List<Medicinecart>> medicinelist;

    public MedicineRepo(Application application) {
        MedicineDatabase medicineDatabase = MedicineDatabase.getInstance(application);
        medicineDAO = medicineDatabase.medicineDAO();
        medicinelist = medicineDAO.getAllData();
    }
    public void insertData(Medicinecart medicinecart) {
        new InsertTask(medicineDAO).execute(medicinecart);
    }

    public void updateData(Medicinecart medicinecart) {

        new UpdateTask(medicineDAO).execute(medicinecart);
    }

    public void deleteData(Medicinecart medicinecart) {
        new DeleteTask(medicineDAO).execute(medicinecart);
    }

    public LiveData<List<Medicinecart>> getAllData() {
        return medicinelist;
    }
    private static class InsertTask extends AsyncTask<Medicinecart, Void, Void> {
        private MedicineDAO medicineDAO;

        public InsertTask(MedicineDAO medicineDAO) {
            this.medicineDAO = medicineDAO;
        }

        @Override
        protected Void doInBackground(Medicinecart... medicinecarts) {
            medicineDAO.insert(medicinecarts[0]);
            return null;
        }
    }
    private static class UpdateTask extends AsyncTask<Medicinecart, Void, Void> {

        private MedicineDAO medicineDAO;


        public UpdateTask(MedicineDAO medicineDAO) {
            this.medicineDAO = medicineDAO;
        }

        @Override
        protected Void doInBackground(Medicinecart... medicinecarts) {
            medicineDAO.update(medicinecarts[0]);
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Medicinecart, Void, Void> {
        private MedicineDAO medicineDAO;

        public DeleteTask(MedicineDAO medicineDAO) {
            this.medicineDAO = medicineDAO;
        }


        @Override
        protected Void doInBackground(Medicinecart... medicinecarts) {
            medicineDAO.delete(medicinecarts[0]);
            return null;
        }
    }
}

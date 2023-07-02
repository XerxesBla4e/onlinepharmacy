package com.example.fixit.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fixit.Model.Medicinecart;
import com.example.fixit.Repository.MedicineRepo;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {
    private MedicineRepo medicineRepo;
    private LiveData<List<Medicinecart>> medicineList;

    public MedicineViewModel(Application application) {
        super(application);
        medicineRepo = new MedicineRepo(application);
        medicineList = medicineRepo.getAllData();
    }

    public void insert(Medicinecart medicinecart) {
        medicineRepo.insertData(medicinecart);
    }

    public void delete(Medicinecart medicinecart) {
        medicineRepo.deleteData(medicinecart);
    }

    public void update(Medicinecart medicinecart) {
        medicineRepo.updateData(medicinecart);
    }

    public LiveData<List<Medicinecart>> getAllMeds() {
        return medicineList;
    }
}

package com.example.fixit.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fixit.Model.Medicinecart;

import java.util.List;

@Dao
public interface MedicineDAO {
    @Insert
    public void insert(Medicinecart medicinecart);
    @Update
    public void update(Medicinecart medicinecart);
    @Delete
    public void delete(Medicinecart medicinecart);


    @Query("SELECT * FROM my_cart")
    public LiveData<List<Medicinecart>> getAllData();
}

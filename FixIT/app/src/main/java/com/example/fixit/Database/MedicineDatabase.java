package com.example.fixit.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fixit.DAO.MedicineDAO;
import com.example.fixit.Model.Medicinecart;

@Database(entities = Medicinecart.class, version = 1)
public abstract class MedicineDatabase extends RoomDatabase {
    private static MedicineDatabase instance;

    public abstract MedicineDAO medicineDAO();

    public static synchronized MedicineDatabase getInstance(Context context) {
        if (null == instance) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MedicineDatabase.class, "medicine_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}

package menum.menum.InterfacePackage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import menum.menum.RoomDatabase.Model.Customer;

/**
 * Created by deniz on 8.1.2018.
 */

@Dao
public interface CustomerUserDao {
    @Query("select * from Customer")
    List<Customer> getAllInfo();

    @Insert
    void inserUser(Customer... customers);

    @Update
    void updateUser(Customer customer);
}

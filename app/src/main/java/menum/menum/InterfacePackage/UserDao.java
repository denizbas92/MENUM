package menum.menum.InterfacePackage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import menum.menum.RoomDatabase.Model.User;

/**
 * Created by deniz on 15.11.2017.
 */

@Dao
public interface UserDao {
    @Query("select * from User")
    List<User> getAllUser();

    @Insert
    void inserUser(User... users);

    @Update
    void updateUser(User user);
}

package menum.menum.RoomDatabase.AbstractPackage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import menum.menum.InterfacePackage.UserDao;
import menum.menum.RoomDatabase.Model.User;

/**
 * Created by deniz on 15.11.2017.
 */

@Database(entities = {User.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

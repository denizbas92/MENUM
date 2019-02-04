package menum.menum.RoomDatabase.AbstractPackage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import menum.menum.RoomDatabase.Model.Customer;
import menum.menum.InterfacePackage.CustomerUserDao;
/**
 * Created by deniz on 8.1.2018.
 */

@Database(entities = {Customer.class},version = 1,exportSchema = false)
public abstract class CustomerAppDatabase extends RoomDatabase{
    public abstract CustomerUserDao customerUserDao();
}

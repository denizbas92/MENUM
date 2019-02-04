package menum.menum.AdviceNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.Model.NewNotificationSettings;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.R;
import menum.menum.RoomDatabase.AbstractPackage.AppDatabase;
import menum.menum.RoomDatabase.Model.User;

/**
 * Created by deniz on 7.2.2018.
 */

public class AdviceService extends Service {

    private static String phoneNumber;
    private List<User> listUserInfo;
    private AdviceGetResult adviceGetResult=new AdviceGetResult();

    private DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
    private static final  int ID=0004;
    boolean netState=false;

    NotificationCompat.Builder notBuilder;
    Intent intenT;
    PendingIntent pendingIntent;
    private static int [] notificationProperty= new int[3];

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        intenT = new Intent(this, LoginActivity.class);
        pendingIntent=PendingIntent.getActivity(this,0,intenT,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase ad= Room.databaseBuilder(this,AppDatabase.class,"UsersInfo")
                .allowMainThreadQueries()
                .build();
        listUserInfo=ad.userDao().getAllUser();
        phoneNumber=listUserInfo.get(0).getPhoneNumber();

        notBuilder=new NotificationCompat.Builder(this);
        notBuilder.setAutoCancel(true);

        final Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot snapData) {
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.NOTIFICATIONS)
                                        .child(MenumConstant.ADVICE_NOTIFICATION)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try{
                                                    NewNotificationSettings notificationSettings=snapData.getValue(NewNotificationSettings.class);
                                                    NotificationCounterPost counterPost=dataSnapshot.getValue(NotificationCounterPost.class);
                                                    String oldCounter=counterPost.getOldCounter();
                                                    String newCounter=counterPost.getNewCounter();
                                                    new AdviceGetResult(oldCounter,newCounter);
                                                    if(adviceGetResult.isEqual()==1){
                                                        if(!netState){
                                                            if(notificationSettings.getCloseNotification().equalsIgnoreCase("false")){
                                                                notBuilder.setSmallIcon(R.mipmap.ic_menu);
                                                                notBuilder.setWhen(System.currentTimeMillis());
                                                                notBuilder.setContentTitle("MENUM");
                                                                notBuilder.setContentText("Öneri/Şikayet Kutunuzda Yeni Bir Mesajınız Var !");
                                                                notBuilder.setContentIntent(pendingIntent);

                                                                if(notificationSettings.getCloseBeep().equalsIgnoreCase("false")){
                                                                    notificationProperty[0]=1;
                                                                }else{
                                                                    notificationProperty[0]=100;
                                                                }
                                                                if(notificationSettings.getCloseLight().equalsIgnoreCase("false")){
                                                                    notificationProperty[1]=4;
                                                                }else{
                                                                    notificationProperty[1]=100;
                                                                }
                                                                if(notificationSettings.getCloseVibration().equalsIgnoreCase("false")){
                                                                    notificationProperty[2]=2;
                                                                }else{
                                                                    notificationProperty[2]=100;
                                                                }

                                                                notBuilder.setDefaults(notificationProperty[0] | notificationProperty[1] | notificationProperty[2]);
                                                                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                                                nm.notify(ID,notBuilder.build());
                                                            }



                                                            netState=true;

                                                            NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                                                            notificationCounterPost.setNewCounter("0");
                                                            databaseReference
                                                                    .child(MenumConstant.STORE)
                                                                    .child(phoneNumber)
                                                                    .child(MenumConstant.NOTIFICATIONS)
                                                                    .child(MenumConstant.ADVICE_NOTIFICATION)
                                                                    .child("newCounter")
                                                                    .setValue(notificationCounterPost.getNewCounter());
                                                        }
                                                    }else{
                                                        netState=false;
                                                    }
                                                }catch (Exception e){

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                handler.postDelayed(this,3000);
            }
        };
        handler.post(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
}

package menum.menum.CampaignPackage;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ShowCampaignFromNotification;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.MainScreenPackage.AddCampaign;
import menum.menum.Model.CustomerCampaignNotPost;
import menum.menum.Model.GoToCampaignPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.R;
import menum.menum.RoomDatabase.Model.User;

/**
 * Created by deniz on 8.2.2018.
 */

public class CampaignService extends Service {

    private static Context context;
    private static String phoneNumber;
    private static String storeName;
    private static String publishDate;
    private static String publishHour;

    private CampaignGetResult campaignGetResult=new CampaignGetResult();

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private static final  int ID=0003;
    boolean netState=false;

    NotificationCompat.Builder notBuilder;
    Intent intenT;
    PendingIntent pendingIntent;

    public CampaignService(){

    }

    public CampaignService(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notBuilder=new NotificationCompat.Builder(this);
        notBuilder.setAutoCancel(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Handler handler=new Handler();
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot snapData) {
                                            databaseReference
                                                    .child(MenumConstant.CUSTOMERS)
                                                    .child(getImeiNumber())
                                                    .child(MenumConstant.NEW_CAMPAIGN)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            try{
                                                                CustomerCampaignNotPost customerCampaignNotPost=snapData.getValue(CustomerCampaignNotPost.class);
                                                                NotificationCounterPost counterPost=dataSnapshot.getValue(NotificationCounterPost.class);
                                                                String oldCounter=counterPost.getOldCounter();
                                                                String newCounter=counterPost.getNewCounter();
                                                                new CampaignGetResult(oldCounter,newCounter);
                                                                if(campaignGetResult.isEqual()==1){
                                                                    if(!netState){
                                                                        if(customerCampaignNotPost.getIsClose().equalsIgnoreCase("false")){
                                                                            databaseReference
                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                    .child(getImeiNumber())
                                                                                    .child(MenumConstant.GOTO_CAMPAIGN)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                            GoToCampaignPost goToCampaignPost=dataSnapshot.getValue(GoToCampaignPost.class);
                                                                                            phoneNumber=goToCampaignPost.getPhoneNumber();
                                                                                            storeName=goToCampaignPost.getStoreName();
                                                                                            publishDate=goToCampaignPost.getPublishDate();
                                                                                            publishHour=goToCampaignPost.getPublishHour();
                                                                                            notBuilder.setSmallIcon(R.mipmap.ic_menu);
                                                                                            notBuilder.setWhen(System.currentTimeMillis());
                                                                                            notBuilder.setContentTitle("MENUM");
                                                                                            notBuilder.setContentText(storeName + " adlı işletmeden yeni kampanya ve duyurunuz var.");
                                                                                            notBuilder.setSubText("Ayrıntılar için tıklayın !");
                                                                                            gotoShowCampaign();
                                                                                            notBuilder.setContentIntent(pendingIntent);
                                                                                            notBuilder.setDefaults(1|4|2);
                                                                                            NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                                                                            nm.notify(ID,notBuilder.build());
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                        }
                                                                                    });


                                                                        }
                                                                        netState=true;
                                                                        NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                                                                        notificationCounterPost.setOldCounter("0");
                                                                        databaseReference
                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                .child(getImeiNumber())
                                                                                .child(MenumConstant.NEW_CAMPAIGN)
                                                                                .child("oldCounter")
                                                                                .setValue(notificationCounterPost.getOldCounter());
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
            },1000);


    }

    private void gotoShowCampaign() {
        new ShowCampaignFromNotification(phoneNumber,storeName,publishDate,publishHour);
        intenT = new Intent(this, ShowCampaignFromNotification.class);
        pendingIntent=PendingIntent.getActivity(this,0,intenT,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public String getImeiNumber(){

        try{
            int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

            if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_STATE},1);
                return "";
            }else{
                String deviceIMEI = telephonyManager.getDeviceId();
                return deviceIMEI;
            }
        }catch (Exception e){
            return "";
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

}

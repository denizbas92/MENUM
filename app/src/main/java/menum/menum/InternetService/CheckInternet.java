package menum.menum.InternetService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by deniz on 27.10.2017.
 */

public class CheckInternet extends Service {
    ConnectionDetector connectionDetector= new ConnectionDetector(this);
    boolean netState=false;
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("Servis","Başladı");

    }

    @Override
    public void onCreate() {
        super.onCreate();


        final Handler handler= new Handler();
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                if(!connectionDetector.isConnection()){
                    if(!netState){

                        Toast.makeText(getApplicationContext(),"İnternet Bağlantınızı Kontrol Ediniz", Toast.LENGTH_LONG).show();
                  //      netState=true;
                    }
                }else{
                    netState=false;
                }
                handler.postDelayed(this,10000);
            }
        };
        handler.post(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Servis","Durdu");
        super.onDestroy();
    }
}
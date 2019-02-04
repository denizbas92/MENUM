package menum.menum.InternetService;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    Context context ;

    public ConnectionDetector(Context context) {
        this.context=context;
    }

    public boolean isConnection(){
        ConnectivityManager connectivityManager =(ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null){
            NetworkInfo[] which=connectivityManager.getAllNetworkInfo();

            for (NetworkInfo netInfo : which) {
                if(netInfo!=null){
                    if(netInfo.getState()== NetworkInfo.State.CONNECTED){
                        if(netInfo.getTypeName().equalsIgnoreCase("WIFI")){
                            if(netInfo.isConnected()==true){
                                //       Toast.makeText(context,"Wifi",Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")){
                            if(netInfo.isConnected()==true){
                                //       Toast.makeText(context,"Mobile Phone",Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

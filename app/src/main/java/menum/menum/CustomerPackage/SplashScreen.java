package menum.menum.CustomerPackage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.R;

public class SplashScreen extends AppCompatActivity {

    private LinearLayout top,bottom;
    private Animation uptoDown,downtoUp;
    private TextView tvStoreName;
    private CircleImageView imStoreIcon;
    private static String phoneNumber;
    private static String storeName ;
    private static String iconStore;

    public SplashScreen(){

    }

    public SplashScreen(String phoneNumber, String storeName, String iconStore) {
        this.phoneNumber=phoneNumber;
        this.storeName=storeName;
        this.iconStore=iconStore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CustomerMainScreen(phoneNumber);
                startActivity(new Intent(SplashScreen.this,CustomerMainScreen.class));
                finish();
            }
        },3000);
    }

    private void init() {
        top=findViewById(R.id.top);
        bottom=findViewById(R.id.bottom);
        imStoreIcon=findViewById(R.id.imStoreIcon);
        tvStoreName=findViewById(R.id.tvStoreName);
        tvStoreName.setText(storeName);
        if(iconStore.equalsIgnoreCase("")==false){
            Glide.with(SplashScreen.this).load(iconStore).fitCenter().into(imStoreIcon);
        }
        uptoDown= AnimationUtils.loadAnimation(this,R.anim.upto_down);
        downtoUp= AnimationUtils.loadAnimation(this,R.anim.downto_up);
        top.setAnimation(uptoDown);
        bottom.setAnimation(downtoUp);
    }
}

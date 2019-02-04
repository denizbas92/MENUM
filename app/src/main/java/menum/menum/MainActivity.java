package menum.menum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import menum.menum.CustomerPackage.CustomerMainScreen;
import menum.menum.CustomerPackage.FirstScreen;
import menum.menum.CustomerPackage.SplashScreen;
import menum.menum.LoginScreenPackage.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnManagement,btnCustomer;
    private TextView tvMenum;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        animMenum();
        btnManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FirstScreen.class));
                finish();

            }
        });
    }

    private void init() {
        btnManagement=findViewById(R.id.btnManagement);
        btnCustomer=findViewById(R.id.btnCustomer);
        tvMenum=findViewById(R.id.tvMenum);
    }

    private void animMenum() {
        Animation anim_fade= AnimationUtils.loadAnimation(this,R.anim.anim_fade);
        tvMenum.setAnimation(anim_fade);
    }
}

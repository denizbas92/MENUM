package menum.menum.BackgroundImagePackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import menum.menum.MainScreenPackage.Settings;
import menum.menum.R;
import menum.menum.ViewPagerPackage.PagerViewAdapter;

public class BackgroundImage extends AppCompatActivity {

    private static Context context;
    private TextView tvImage1,tvImage2,tvImage3;
    private ViewPager mainViewPager;

    private PagerViewAdapter pagerViewAdapter;

    private static String phoneNumber ;

    public BackgroundImage(){

    }

    public BackgroundImage(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BackgroundImage.this, Settings.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_image);
        init();

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(0);
            }
        });
        tvImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(1);
            }
        });
        tvImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(2);
            }
        });
    }

    private void init() {
        tvImage1=findViewById(R.id.tvImage1);
        tvImage2=findViewById(R.id.tvImage2);
        tvImage3=findViewById(R.id.tvImage3);
        mainViewPager=findViewById(R.id.mainViewPager);
        mainViewPager.setOffscreenPageLimit(3);

        pagerViewAdapter=new PagerViewAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(pagerViewAdapter);
    }
    private void changeTabs(int position) {

        DisplayMetrics metrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth=metrics.widthPixels;
        int screenHeight=metrics.heightPixels;
        double wi=(double)screenWidth/(double)metrics.xdpi;
        double hi=(double)screenHeight/(double)metrics.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        final double screenInches = Math.sqrt(x+y);

        if (screenInches<5){
            setPageTitle(position,22,15);
        }else if(screenInches>5 && screenInches<7){
            setPageTitle(position,32,25);
        }else{
            setPageTitle(position,42,35);
        }

    }

    private void setPageTitle(int tempPosition, int first, int second) {
        if(tempPosition==0){
            tvImage1.setTextSize(first);
            tvImage1.setTextColor(Color.parseColor("#ffffff"));

            tvImage2.setTextSize(second);
            tvImage2.setTextColor(Color.parseColor("#e9d2d2"));

            tvImage3.setTextSize(second);
            tvImage3.setTextColor(Color.parseColor("#e9d2d2"));
        }
        if(tempPosition==1){
            tvImage1.setTextSize(second);
            tvImage1.setTextColor(Color.parseColor("#e9d2d2"));
            tvImage2.setTextSize(first);
            tvImage2.setTextColor(Color.parseColor("#ffffff"));
            tvImage3.setTextSize(second);
            tvImage3.setTextColor(Color.parseColor("#e9d2d2"));
        }

        if(tempPosition==2){
            tvImage1.setTextSize(second);
            tvImage1.setTextColor(Color.parseColor("#e9d2d2"));
            tvImage2.setTextSize(second);
            tvImage2.setTextColor(Color.parseColor("#e9d2d2"));
            tvImage3.setTextSize(first);
            tvImage3.setTextColor(Color.parseColor("#ffffff"));
        }
    }
}

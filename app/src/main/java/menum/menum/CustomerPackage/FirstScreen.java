package menum.menum.CustomerPackage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import menum.menum.Constant.MenumConstant;
import menum.menum.InternetService.CheckInternet;
import menum.menum.MainScreenPackage.AddProduct;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.Model.StorePropertyPost;
import menum.menum.R;

import static java.lang.Long.decode;

public class FirstScreen extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int CAPTURE_CAMERA = 1;
    private Button btnEnter;
    private ZXingScannerView scannerView;
    private static String decodedPhoneNumber="";
    private static String phoneNumber;
    private DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(FirstScreen.this, CheckInternet.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(FirstScreen.this, CheckInternet.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        init();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck= ContextCompat.checkSelfPermission(FirstScreen.this, Manifest.permission.CAMERA);
                if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FirstScreen.this,new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    setContentView(scannerView);
                    scannerView.startCamera();
                }
            }
        });
    }

    private void init() {
        btnEnter=findViewById(R.id.btnEnter);
        scannerView=new ZXingScannerView(FirstScreen.this);
        scannerView.setResultHandler((ZXingScannerView.ResultHandler) this);
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void handleResult(Result result) {
        if(result!=null){
            scannerView.stopCamera();
            try{
                decode(result.getText().toString());
                hasPhoneNumber();
            }catch (Exception e){
                StyleableToast st=new StyleableToast(FirstScreen.this,"Kayıt Bulunamadı.", Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#FF5A5F"));
                st.setCornerRadius(2);
                st.show();
                finish();
            }

        }
    }

    private void hasPhoneNumber() {
        databaseReference
                .child(MenumConstant.STORE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(phoneNumber)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.STORE_PROPERTY)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                            new SplashScreen(phoneNumber,storePropertyPost.getStoreName(),storePropertyPost.getIconStore());
                                            startActivity(new Intent(FirstScreen.this,SplashScreen.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else{
                            StyleableToast st=new StyleableToast(FirstScreen.this,"Kayıt Bulunamadı.", Toast.LENGTH_LONG);
                            st.setBackgroundColor(Color.parseColor("#FF5A5F"));
                            st.setCornerRadius(2);
                            st.show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void decode(String scanResult){
        String [] totalString=scanResult.split("#!");
        String [] phoneNumber=totalString[0].split("&");
        int amountIncrease=Integer.parseInt(String.valueOf(phoneNumber[0].charAt(1)));
        int divideAmountIncrease=amountIncrease/2;

        for(int k=0 ; k<phoneNumber.length-1 ; k++){
            phoneNumber[k]=phoneNumber[k+1];
        }

        String [][] multiTotalString={phoneNumber};

   //     Log.d("multiDimension", String.valueOf(multiTotalString[0].length) + " " + String.valueOf(multiTotalString[1].length) + "  " + String.valueOf(multiTotalString[2].length));

        for(int j=0 ; j<phoneNumber.length ; j++){
            Log.d("phone",phoneNumber[j]);
        }
        for(int i=0 ; i<1 ; i++){
            for(int j=0 ; j<multiTotalString[i].length ; j++){
                if(j%2==0){
                    if(multiTotalString[i][j].length()>4){
                        int valueInteger=Integer.parseInt(multiTotalString[i][j].substring(1,4))-amountIncrease;
                        char valueChar=(char)valueInteger;
                        decodedPhoneNumber+=Character.toString(valueChar);
                    }else{
                        int valueInteger=Integer.parseInt(multiTotalString[i][j].substring(1,3))-amountIncrease;
                        char valueChar=(char)valueInteger;
                        decodedPhoneNumber+=Character.toString(valueChar);
                    }
                }else{
                    if(multiTotalString[i][j].length()>4){
                        int valueInteger=Integer.parseInt(multiTotalString[i][j].substring(1,4))+divideAmountIncrease;
                        char valueChar=(char)valueInteger;
                        decodedPhoneNumber+=Character.toString(valueChar);
                    }else{
                        int valueInteger=Integer.parseInt(multiTotalString[i][j].substring(1,3))+divideAmountIncrease;
                        char valueChar=(char)valueInteger;
                        decodedPhoneNumber+=Character.toString(valueChar);
                    }
                }
            }
            decodedPhoneNumber+="#";
        }

        String tempValue[]=decodedPhoneNumber.split("#");
        this.phoneNumber=tempValue[0].substring(0,tempValue[0].length()-1);

    }
}

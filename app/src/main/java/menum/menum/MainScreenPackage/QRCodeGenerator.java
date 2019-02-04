package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.Constant.MenumConstant;
import menum.menum.Model.AddProductPost;
import menum.menum.Model.StorePropertyPost;
import menum.menum.R;

public class QRCodeGenerator extends AppCompatActivity {

    private ImageView imQR;
    private CircleImageView imStoreIcon;
    private TextView tvStoreName;
    private static String phoneNumber;
    private static String encodedPhoneNumber="";
    private MultiFormatWriter formatWriter;

    private DatabaseReference databaseReference;

    public QRCodeGenerator(){

    }

    public QRCodeGenerator(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QRCodeGenerator.this,MainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);
        init();
        setStoreInfo();
        encode();
        createQrCode();

    }

    private void setStoreInfo() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.STORE_PROPERTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                        if(storePropertyPost.getIconStore().equalsIgnoreCase("")==false){
                            Glide.with(QRCodeGenerator.this).load(storePropertyPost.getIconStore()).into(imStoreIcon);
                        }
                        tvStoreName.setText(storePropertyPost.getStoreName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void init() {
        imQR=findViewById(R.id.imQR);
        tvStoreName=findViewById(R.id.tvStoreName);
        imStoreIcon=findViewById(R.id.imStoreIcon);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        formatWriter= new MultiFormatWriter();
    }

    private void createQrCode() {
        try {
            BitMatrix matrix=formatWriter.encode(encodedPhoneNumber, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder encoder=new BarcodeEncoder();
            Bitmap bitmap=encoder.createBitmap(matrix);
            imQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void encode() {
        char[] cPhoneNumber=phoneNumber.toCharArray();

        int asciiArray [];
        int lengthCharArrays[]={cPhoneNumber.length};
        char[][] cTotal={cPhoneNumber};

        Random random= new Random();
        int amountIncrease=random.nextInt(9)+1; // artış miktarini belirleyen random sayısı değeri
        int divideAmountIncrease=amountIncrease/2; // azalış miktarını belirten random sayısı değeri

        int firstR=random.nextInt(87)+39;
        int thirdR=random.nextInt(87)+39;
        char first= (char) firstR;
        char third=(char)thirdR;

        encodedPhoneNumber=first+Integer.toString(amountIncrease)+third+"&";

        for(int i=0 ; i<1 ; i++){
            asciiArray= new int[lengthCharArrays[i]];
            for(int j=0 ; j<lengthCharArrays[i] ; j++){
                asciiArray[j]=cTotal[i][j];
                int firstRandom=random.nextInt(87)+39;
                int thirdRandom=random.nextInt(87)+39;
                char firstChar= (char) firstRandom;
                char thirdChar=(char)thirdRandom;
                if(j%2==0){
                    encodedPhoneNumber+=firstChar+Integer.toString(asciiArray[j] + amountIncrease) +thirdChar+"&";
                }else{
                    encodedPhoneNumber+=firstChar+Integer.toString(asciiArray[j] - divideAmountIncrease)+thirdChar+ "&";
                }
            }
            encodedPhoneNumber+="#!";
        }
 //       tvEncoded.setText(encodedPhoneNumber);
        Log.d("QRCode",encodedPhoneNumber);
    }
}

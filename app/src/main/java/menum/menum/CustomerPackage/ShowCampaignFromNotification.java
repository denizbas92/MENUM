package menum.menum.CustomerPackage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import menum.menum.Constant.MenumConstant;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.Model.CampaignPost;
import menum.menum.Model.GoToCampaignPost;
import menum.menum.R;

public class ShowCampaignFromNotification extends AppCompatActivity {

    private TextView tvStoreName,tvPublishDate,tvPublishHour;
    private TextView etCampaignTitle,etCampaignContent;
    private Button btnExit;
    private Switch swNotification;

    private static String phoneNumber;
    private static String storeName;
    private static String publishDate;
    private static String publishHour;

    private DatabaseReference databaseReference;

    public ShowCampaignFromNotification(){

    }

    public ShowCampaignFromNotification(String phoneNumber, String storeName, String publishDate, String publishHour) {
        this.phoneNumber=phoneNumber;
        this.storeName=storeName;
        this.publishDate=publishDate;
        this.publishHour=publishHour;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GoToCampaignPost goToCampaignPost=new GoToCampaignPost("","","","");
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.GOTO_CAMPAIGN)
                .setValue(goToCampaignPost);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoToCampaignPost goToCampaignPost=new GoToCampaignPost("","","","");
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.GOTO_CAMPAIGN)
                .setValue(goToCampaignPost);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_campaign_from_notification);
        init();
        setCampaign();
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToCampaignPost goToCampaignPost=new GoToCampaignPost("","","","");
                databaseReference
                        .child(MenumConstant.CUSTOMERS)
                        .child(getImeiNumber())
                        .child(MenumConstant.GOTO_CAMPAIGN)
                        .setValue(goToCampaignPost);
                finish();
            }
        });

        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean result) {
                if(result){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ShowCampaignFromNotification.this);
                    View view= LayoutInflater.from(ShowCampaignFromNotification.this).inflate(R.layout.layout_confirm_dialog,null);
                    TextView tvTitle=view.findViewById(R.id.tvTitle);
                    tvTitle.setText("Bu işletmeden gelen bildirimler engellenecek. Bunu onaylıyor musunuz ?");
                    Button btnCancel=view.findViewById(R.id.btnCancel);
                    Button btnConfirm=view.findViewById(R.id.btnConfirm);
                    builder.setView(view);
                    final AlertDialog dialog=builder.create();
                    dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                    dialog.show();

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            swNotification.setChecked(false);
                            dialog.dismiss();
                        }
                    });

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                                    .child("isClose").setValue("true");
                            StyleableToast st=new StyleableToast(ShowCampaignFromNotification.this,"Bildirim Alımları Kapatıldı", Toast.LENGTH_LONG);
                            st.setBackgroundColor(Color.parseColor("#ff0000"));
                            st.setTextColor(Color.WHITE);
                            st.setCornerRadius(2);
                            st.show();
                            dialog.dismiss();
                        }
                    });
                }else{
                    swNotification.setChecked(false);
                    databaseReference
                            .child(MenumConstant.CUSTOMERS)
                            .child(getImeiNumber())
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                            .child("isClose").setValue("false");
                    StyleableToast st=new StyleableToast(ShowCampaignFromNotification.this,"Bildirimler Açıldı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#0000ff"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                }
            }
        });
    }

    private void init() {
        tvStoreName=findViewById(R.id.tvStoreName);
        tvPublishDate=findViewById(R.id.tvPublishDate);
        tvPublishHour=findViewById(R.id.tvPublishHour);
        etCampaignTitle=findViewById(R.id.etCampaignTitle);
        etCampaignContent=findViewById(R.id.etCampaignContent);
        btnExit=findViewById(R.id.btnExit);
        swNotification=findViewById(R.id.swNotification);

        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void setCampaign() {

        tvStoreName.setText(storeName);
        tvPublishDate.setText(publishDate);
        tvPublishHour.setText(publishHour);
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.CAMPAIGN)
                .child(publishDate)
                .child(publishHour)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CampaignPost campaignPost=dataSnapshot.getValue(CampaignPost.class);
                        etCampaignTitle.setText(campaignPost.getTitle());
                        etCampaignContent.setText(campaignPost.getCampaignDetail());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public String getImeiNumber(){
        int permissionCheck= ContextCompat.checkSelfPermission(ShowCampaignFromNotification.this, Manifest.permission.READ_PHONE_STATE);
        TelephonyManager telephonyManager = (TelephonyManager) ShowCampaignFromNotification.this.getSystemService(TELEPHONY_SERVICE);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) ShowCampaignFromNotification.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            return "";
        }else{
            String deviceIMEI = telephonyManager.getDeviceId();
            return deviceIMEI;
        }
    }
}

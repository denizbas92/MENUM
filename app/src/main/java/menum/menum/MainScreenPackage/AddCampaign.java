package menum.menum.MainScreenPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import menum.menum.CampaignPackage.CampaignService;
import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.Model.CampaignPost;
import menum.menum.Model.CommentPost;
import menum.menum.Model.GoToCampaignPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.Model.StorePropertyPost;
import menum.menum.R;

public class AddCampaign extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etCampaignTitle,etCampaignContent;
    private Button btnBack,btnSave;

    private static String phoneNumber;

    private DatabaseReference databaseReference;

    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    public AddCampaign(){

    }

    public AddCampaign(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddCampaign.this,Campaigns.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campaign);

        init();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCampaing();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCampaign.this,Campaigns.class));
                finish();
            }
        });
    }

    private void init() {
        tvTitle=findViewById(R.id.tvTitle);
        etCampaignTitle=findViewById(R.id.etCampaignTitle);
        etCampaignContent=findViewById(R.id.etCampaignContent);
        btnBack=findViewById(R.id.btnBack);
        btnSave=findViewById(R.id.btnSave);
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void saveCampaing() {
        if(TextUtils.isEmpty(etCampaignTitle.getText().toString())){
            StyleableToast st=new StyleableToast(AddCampaign.this,"Kampanya Başlığı Giriniz", Toast.LENGTH_SHORT);
            st.setBackgroundColor(Color.parseColor("#ff0000"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();
            return;
        }
        if(TextUtils.isEmpty(etCampaignContent.getText().toString())){
            StyleableToast st=new StyleableToast(AddCampaign.this,"Kampanya İçeriği Giriniz", Toast.LENGTH_SHORT);
            st.setBackgroundColor(Color.parseColor("#ff0000"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();
            return;
        }

        String title=etCampaignTitle.getText().toString();
        String content=etCampaignContent.getText().toString();

        new GetTimeFromNet(title,content).execute();
    }

    public String getCurrentDate() {
        Calendar calendar ;
        SimpleDateFormat simpleDateFormat ;
        String date ;

        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        date=simpleDateFormat.format(calendar.getTime());
        return date;
    }

    public String getCurrentTime() {
        Calendar calendar ;
        SimpleDateFormat simpleDateFormat ;
        String time ;

        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("HH:mm");
        time=simpleDateFormat.format(calendar.getTime());
        return time;
    }

    public class GetTimeFromNet extends AsyncTask<Void,Void,Void> {
        Context context;
        private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String title ;
        String content;
        public GetTimeFromNet(String title, String content) {
            this.title=title;
            this.content=content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document documentHour= Jsoup.connect(baseUrlHour).get();
                Document documentDate= Jsoup.connect(baseUrlHour).get();
                Elements elementHour=documentHour.select("span[id=clk_hm]");
                Elements elementDate=documentDate.select("span[id=ij2]");
                date=elementDate.text() ;
                hour=elementHour.text();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("girdiHata",e.getMessage().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                String [] splitDate=date.split(" ");
                int iDate=Integer.parseInt(splitDate[0]);
                int cDate=Integer.parseInt(getCurrentDate().substring(0,2));

                int iHour=Integer.parseInt(hour.substring(0,2));
                int iMinute=Integer.parseInt(hour.substring(3,5));

                int cHour=Integer.parseInt(getCurrentTime().substring(0,2));
                int cMinute=Integer.parseInt(getCurrentTime().substring(3,5));

                int tempMinute=Math.abs(cMinute-iMinute);
                if(iDate==cDate){
                    if(tempMinute<50){
                        addCampaign(title,content);
                    }else{
                        Toast.makeText(AddCampaign.this,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddCampaign.this,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                addCampaign(title,content);
            }
        }
    }

    private void addCampaign(String title, String content) {
        CampaignPost campaignPost=new CampaignPost(title,content,getCurrentDate(),getCurrentTime());
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.CAMPAIGN)
                .child(getCurrentDate())
                .child(getCurrentTime())
                .setValue(campaignPost);

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MYCUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImei:dataSnapshot.getChildren()){
                            NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                            notificationCounterPost.setOldCounter("1");
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImei.getKey().toString())
                                    .child(MenumConstant.NEW_CAMPAIGN)
                                    .child("oldCounter")
                                    .setValue(notificationCounterPost.getOldCounter());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.STORE_PROPERTY)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                            GoToCampaignPost goToCampaignPost=new GoToCampaignPost(phoneNumber,storePropertyPost.getStoreName(),getCurrentDate(),getCurrentTime());
                                            databaseReference
                                                    .child(MenumConstant.CUSTOMERS)
                                                    .child(snapImei.getKey().toString())
                                                    .child(MenumConstant.GOTO_CAMPAIGN)
                                                    .setValue(goToCampaignPost);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        StyleableToast st=new StyleableToast(AddCampaign.this,"Kampanya Başarıyla Eklendi", Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_tick);
        st.setCornerRadius(2);
        st.show();

        startActivity(new Intent(AddCampaign.this,Campaigns.class));
        finish();
    }
}

package menum.menum.CustomerPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import menum.menum.Constant.MenumConstant;
import menum.menum.Model.AdviceComplaintPost;
import menum.menum.Model.NewNotificationPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.R;

public class AdviceComplaint extends AppCompatActivity {

    private EditText etContent,etTitle;
    private Button btnBack;
    private Button btnSend;

    private static String phoneNumber;

    private DatabaseReference databaseReference;
    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    public AdviceComplaint(){

    }

    public AdviceComplaint(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdviceComplaint.this,CustomerMainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice_complaint);
        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdviceComplaint.this,CustomerMainScreen.class));
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAdviceComplaint();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        btnSend=findViewById(R.id.btnSend);
        etContent=findViewById(R.id.etContent);
        etTitle=findViewById(R.id.etTitle);
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void sendAdviceComplaint() {
        if(TextUtils.isEmpty(etTitle.getText().toString())){
            StyleableToast st=new StyleableToast(AdviceComplaint.this,"Başlık Giriniz", Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#ff0000"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();
            return;
        }

        if(TextUtils.isEmpty(etContent.getText().toString())){
            StyleableToast st=new StyleableToast(AdviceComplaint.this,"İçerik Giriniz", Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#ff0000"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();
            return;
        }

        String title=etTitle.getText().toString();
        String content=etContent.getText().toString();

        new GetTimeFromNet(AdviceComplaint.this,title,content).execute();
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
        private String title,content;

        public GetTimeFromNet(AdviceComplaint adviceComplaint, String title, String content) {
            this.context=adviceComplaint;
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
                        AdviceComplaintPost adviceComplaintPost=new AdviceComplaintPost(title,content,getCurrentDate(),getCurrentTime(),"false");
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.ADVICE_COMPLAINT)
                                .child(getCurrentDate())
                                .child(getCurrentTime())
                                .setValue(adviceComplaintPost);


                        NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                        notificationCounterPost.setNewCounter("1");
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.NOTIFICATIONS)
                                .child(MenumConstant.ADVICE_NOTIFICATION)
                                .child("newCounter")
                                .setValue(notificationCounterPost.getNewCounter());

                        NewNotificationPost newNotificationPost=new NewNotificationPost();
                        newNotificationPost.setDate(getCurrentDate());
                        newNotificationPost.setHour(getCurrentTime());
                        newNotificationPost.setNotification("Öneri/Şikayet kutunuzda yeni mesajınız bulunmaktadır.");
                        newNotificationPost.setSeen("false");
                        newNotificationPost.setProductName("");
                        newNotificationPost.setMenuCategoryName("");
                        newNotificationPost.setCodeNot("2");
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.NOTIFICATIONS)
                                .child(MenumConstant.NEW_NOTIFICATION)
                                .child(getCurrentDate())
                                .child(getCurrentTime())
                                .setValue(newNotificationPost);

                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        View view= LayoutInflater.from(context).inflate(R.layout.layout_message,null);
                        builder.setView(view);
                        final AlertDialog dialog=builder.create();
                        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                        dialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                startActivity(new Intent(AdviceComplaint.this,CustomerMainScreen.class));
                                finish();
                            }
                        },3000);

                    }else{
                        Toast.makeText(context,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                AdviceComplaintPost adviceComplaintPost=new AdviceComplaintPost(title,content,getCurrentDate(),getCurrentTime(),"false");
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.ADVICE_COMPLAINT)
                        .child(getCurrentDate())
                        .child(getCurrentTime())
                        .setValue(adviceComplaintPost);

                NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                notificationCounterPost.setNewCounter("0");
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.ADVICE_NOTIFICATION)
                        .child("newCounter")
                        .setValue(notificationCounterPost.getNewCounter());

                NewNotificationPost newNotificationPost=new NewNotificationPost();
                newNotificationPost.setDate(getCurrentDate());
                newNotificationPost.setHour(getCurrentTime());
                newNotificationPost.setNotification("Öneri/Şikayet kutunuzda yeni mesajınız bulunmaktadır.");
                newNotificationPost.setSeen("false");
                newNotificationPost.setProductName("");
                newNotificationPost.setMenuCategoryName("");
                newNotificationPost.setCodeNot("2");
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION)
                        .child(getCurrentDate())
                        .child(getCurrentTime())
                        .setValue(newNotificationPost);

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.layout_message,null);
                builder.setView(view);
                final AlertDialog dialog=builder.create();
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        startActivity(new Intent(AdviceComplaint.this,CustomerMainScreen.class));
                        finish();
                    }
                },3000);
            }
        }
    }
}

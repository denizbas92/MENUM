package menum.menum.LoginScreenPackage;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.List;
import java.util.UUID;

import menum.menum.Constant.MenumConstant;
import menum.menum.ForgotPasswordPackage.ForgotPassword;
import menum.menum.MainActivity;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.Model.FreeUsePost;
import menum.menum.Model.NewNotificationSettings;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.Model.UsersPost;
import menum.menum.R;
import menum.menum.RegisterScreenPackage.RegisterBoss;
import menum.menum.RoomDatabase.AbstractPackage.AppDatabase;
import menum.menum.RoomDatabase.Model.User;

public class LoginActivity extends AppCompatActivity {

    private Button btnRegister,btnSignIn;
    private EditText etUserName,etPassword;
    private TextView txtForgetPassword;
    private CheckBox cbRememberMe;

    private DatabaseReference databaseReference;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor;
    private static boolean isCheckBoxClicked;

    private static String userName ;

    private int dayofMonth[]= new int []{31,28,31,30,31,30,31,31,30,31,30,31};
    private List<User> listUserInfo;

    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        isCheckBoxClicked = sharedPref.getBoolean("checked", false);
        if (isCheckBoxClicked == true) {
            cbRememberMe.setChecked(true);
            userName=sharedPref.getString("username","");
            etUserName.setText(userName);
        }
        loginPart();
        registerBoss();
        forgetPassword();
    }

    private void init() {
        btnRegister=findViewById(R.id.btnRegister);
        btnSignIn=findViewById(R.id.btnSignIn);

        etUserName=findViewById(R.id.etUserName);
        etPassword=findViewById(R.id.etPassword);
        etPassword.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});

        cbRememberMe=findViewById(R.id.cbRememberMe);
        txtForgetPassword=findViewById(R.id.txtForgetPassword);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    private void loginPart() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetTimeFromNet().execute();
            }
        });
    }

    private void login() {
        final String userName=etUserName.getText().toString();
        final String password=etPassword.getText().toString();

        if(TextUtils.isEmpty(userName)){
            etUserName.setError("Kullanıcı Adınızı Giriniz");
            return;
        }

        if(TextUtils.isEmpty(password)){
            etPassword.setError("Şifrenizi Giriniz");
            return;
        }

        databaseReference
                .child(MenumConstant.USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userName)){
                            databaseReference
                                    .child(MenumConstant.USERS)
                                    .child(userName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final UsersPost usersPost= dataSnapshot.getValue(UsersPost.class);

                                            if(usersPost.getPassword().equalsIgnoreCase(password)==false){
                                                StyleableToast st=new StyleableToast(LoginActivity.this,"Yanlış Şifre", Toast.LENGTH_LONG);
                                                st.setBackgroundColor(Color.parseColor("#ff0000"));
                                                st.setTextColor(Color.WHITE);
                                                st.setCornerRadius(2);
                                                st.show();
                                                etPassword.setError("Yanlış Şifre");
                                            }else{
                                                rememberMe();
                                                addToRoom(usersPost.getPhoneNumber(),userName);
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(usersPost.getPhoneNumber())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if(dataSnapshot.hasChild(MenumConstant.FREE_USE)){
                                                                        databaseReference
                                                                                .child(MenumConstant.STORE)
                                                                                .child(usersPost.getPhoneNumber())
                                                                                .child(MenumConstant.FREE_USE)
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        final FreeUsePost freeUsePost=dataSnapshot.getValue(FreeUsePost.class);
                                                                                        if(freeUsePost.getLastDate().equalsIgnoreCase(getDate())==false){
                                                                                            // deneme sürümü henüz dolmadı
                                                                                            databaseReference
                                                                                                    .child(MenumConstant.USERS)
                                                                                                    .child(userName)
                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                            UsersPost usersPost1=dataSnapshot.getValue(UsersPost.class);
                                                                                                            if(usersPost1.getIsActive().equalsIgnoreCase("true")){
                                                                                                                new MainScreen(usersPost.getPhoneNumber(),userName,usersPost.getStoreName());
                                                                                                                startActivity(new Intent(LoginActivity.this,MainScreen.class));
                                                                                                                finish();
                                                                                                            }else{
                                                                                                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                                                                                                View view=LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_message,null);
                                                                                                                TextView tvMessage=view.findViewById(R.id.tvMessage);
                                                                                                                tvMessage.setText("Hesabınıza erişim kapatılmıştır. Yetkili ile temasa geçiniz...");
                                                                                                                builder.setView(view);
                                                                                                                final AlertDialog dialog=builder.create();
                                                                                                                dialog.show();

                                                                                                                new Handler().postDelayed(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        dialog.dismiss();
                                                                                                                    }
                                                                                                                },15000);
                                                                                                            }
                                                                                                        }
                                                                                                        @Override
                                                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                                                        }
                                                                                                    });
                                                                                        }else{
                                                                                            databaseReference
                                                                                                    .child(MenumConstant.STORE)
                                                                                                    .child(usersPost.getPhoneNumber())
                                                                                                    .child(MenumConstant.FREE_USE)
                                                                                                    .child("timeOver")
                                                                                                    .setValue("true");
                                                                                            if(freeUsePost.getFirstEnter().equalsIgnoreCase("true")){
                                                                                                databaseReference
                                                                                                        .child(MenumConstant.STORE)
                                                                                                        .child(usersPost.getPhoneNumber())
                                                                                                        .child(MenumConstant.FREE_USE)
                                                                                                        .child("firstEnter")
                                                                                                        .setValue("false");
                                                                                                showFreeUseTimeOver(usersPost,freeUsePost,userName);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                                    }
                                                                                });
                                                                    }else{
                                                                        saveFreeUse(usersPost.getPhoneNumber(),userName,usersPost.getStoreName());
                                                                        showFreeUseInformationMessage(usersPost,userName);
                                                                    }
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
                        }else{
                            etUserName.setError("Kullanıcı Adı Kayıtlı Değil");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void registerBoss() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterBoss.class));
                finish();
            }
        });
    }

    private void forgetPassword() {
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
                finish();
            }
        });
    }

    private void rememberMe() {
        if(cbRememberMe.isChecked()){
            isCheckBoxClicked=cbRememberMe.isChecked();
            editor.putBoolean("checked", isCheckBoxClicked);
            editor.putString("username",etUserName.getText().toString());
            editor.commit();
        }else{
            editor.putBoolean("checked", false);
            editor.commit();
        }
    }

    private void saveFreeUse(String phoneNumber, String userName, String storeName) {
        FreeUsePost freeUsePost=new FreeUsePost(getDate(),calculateFreeIntervalDate(getDate()),"true","false");
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.FREE_USE)
                .setValue(freeUsePost);
    }

    private void showFreeUseTimeOver(final UsersPost usersPost, FreeUsePost freeUseInfo, final String userName) {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        View view= LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_free_use_information,null);
        final TextView tvInformation=view.findViewById(R.id.tvInformation);
        Button btnOk=view.findViewById(R.id.btnOk);
        tvInformation.setText("Başlangıç tarihi " + freeUseInfo.getFirstDate() + " olan 2 aylık deneme sürümünüz an itibari ile sona ermiştir." +
                "Uygulamamızdan aldığınız hizmeti sona erdirmek isterseniz tek yapmanız gerekan AYARLAR kısmından işletme hesabınızı tamamen " +
                "dondurmanızdır. Bu durumda işletmeniz süresiz olarak kapatılır. Ayrıntılı bilgi için onlinemenum@gmail.com dan bizimle irtibata geçebilirniz. Bol kazançlar dileriz.");

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MainScreen(usersPost.getPhoneNumber(),userName,usersPost.getStoreName());
                startActivity(new Intent(LoginActivity.this,MainScreen.class));
                finish();
            }
        });
    }

    private void showFreeUseInformationMessage(final UsersPost usersPost, final String userName) {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        View view= LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_free_use_information,null);
        TextView tvInformation=view.findViewById(R.id.tvInformation);
        Button btnOk=view.findViewById(R.id.btnOk);
        tvInformation.setText("Son tarihi "+calculateFreeIntervalDate(getDate())+" olan 2 aylık deneme sürümü aktifleştirilmiştir. " +
                "Uygulamamız hakkında daha ayrıntılı bilgi almak isterseniz denizbas92@gmail.com adresimizden bize ulaşabilirsiniz." +
                "Bizi seçtiğiniz için teşekkür ederiz. MENÜM");
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();

        NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
        notificationCounterPost.setOldCounter("1");
        notificationCounterPost.setNewCounter("0");
        databaseReference
                .child(MenumConstant.STORE)
                .child(usersPost.getPhoneNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.ADVICE_NOTIFICATION)
                .setValue(notificationCounterPost);
        databaseReference
                .child(MenumConstant.STORE)
                .child(usersPost.getPhoneNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.COMMENT_NOTIFICATION)
                .setValue(notificationCounterPost);
        databaseReference
                .child(MenumConstant.STORE)
                .child(usersPost.getPhoneNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.LIKE_NOTIFICATION)
                .setValue(notificationCounterPost);
        databaseReference
                .child(MenumConstant.STORE)
                .child(usersPost.getPhoneNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.VOTE_NOTIFICATION)
                .setValue(notificationCounterPost);

        NewNotificationSettings newNotificationSettings=new NewNotificationSettings("false","false","false","false");
        databaseReference
                .child(MenumConstant.STORE)
                .child(usersPost.getPhoneNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                .setValue(newNotificationSettings);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MainScreen(usersPost.getPhoneNumber(),userName,usersPost.getStoreName());
                startActivity(new Intent(LoginActivity.this,MainScreen.class));
                finish();
            }
        });
    }

    private String calculateFreeIntervalDate(String date){
        int cDay=Integer.parseInt(date.substring(0,2));
        int cMonth=Integer.parseInt(date.substring(3,5));
        int cYear=Integer.parseInt(date.substring(6));
        String lastDate="";
        int lastMonth=cMonth+2; // 2 aylık deneme sürümü
        if(cMonth<12){
            if(cDay<=dayofMonth[cMonth]){
                if(cDay<10){
                    if(lastMonth<10){
                        lastDate="0"+Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                    }else{
                        if(lastMonth>12){
                            lastMonth=lastMonth-12;
                            lastDate="0"+Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear+1);
                        }else{
                            lastDate="0"+Integer.toString(cDay)+"-"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                        }
                    }
                }else{
                    if(lastMonth<10){
                        lastDate=Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                    }else{
                        if(lastMonth>12){
                            lastMonth=lastMonth-12;
                            lastDate=Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear+1);
                        }else{
                            lastDate=Integer.toString(cDay)+"-"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                        }
                    }
                }

            }else{
                int lastDay=cDay-dayofMonth[cMonth];
                if(cMonth+1<10){
                    lastDate="0"+Integer.toString(lastDay)+"-0"+Integer.toString(cMonth+1)+"-"+Integer.toString(cYear);
                }else{
                    if(cMonth+1>12){
                        cMonth=cMonth+1-12;
                        lastDate="0"+Integer.toString(lastDay)+"-0"+Integer.toString(cMonth)+"-"+Integer.toString(cYear+1);
                    }
                }
            }
        }else{
            // son kullanım ayı şubat olur ise bu döngüye giriyor
            if(cDay<=dayofMonth[1]){
                if(cDay<10){
                    if(lastMonth<10){
                        lastDate="0"+Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                    }else{
                        if(lastMonth>12){
                            lastMonth=lastMonth-12;
                            lastDate="0"+Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear+1);
                        }else{
                            lastDate="0"+Integer.toString(cDay)+"-"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                        }
                    }
                }else{
                    if(lastMonth<10){
                        lastDate=Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                    }else{
                        if(lastMonth>12){
                            lastMonth=lastMonth-12;
                            lastDate=Integer.toString(cDay)+"-0"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear+1);
                        }else{
                            lastDate=Integer.toString(cDay)+"-"+Integer.toString(lastMonth)+"-"+Integer.toString(cYear);
                        }
                    }
                }

            }else{
                int lastDay=dayofMonth[1];
                if(lastDay<10){
                    lastDate="0"+Integer.toString(lastDay)+"-0"+Integer.toString(2)+"-"+Integer.toString(cYear+1);
                }else{
                    lastDate=Integer.toString(lastDay)+"-0"+Integer.toString(2)+"-"+Integer.toString(cYear+1);
                }
            }
        }

        return lastDate;
    }

    private void addToRoom(String phoneNumber, String userName) {
        AppDatabase ad= Room.databaseBuilder(LoginActivity.this,AppDatabase.class,"UsersInfo")
                .allowMainThreadQueries()
                .build();

        listUserInfo=ad.userDao().getAllUser();
        if(listUserInfo.size()==0){
            User newUser=new User(phoneNumber,userName);
            ad.userDao().inserUser(newUser);
        }else{
            listUserInfo=ad.userDao().getAllUser();
            if(listUserInfo.get(0).getPhoneNumber().equalsIgnoreCase(phoneNumber)==false){
                listUserInfo.get(0).setPhoneNumber(phoneNumber);
                ad.userDao().updateUser(listUserInfo.get(0));
            }

            if(listUserInfo.get(0).getUserName().equalsIgnoreCase(userName)==false){
                listUserInfo.get(0).setUserName(userName);
                ad.userDao().updateUser(listUserInfo.get(0));
            }
        }
    }

    public String getDate() {

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
                int cDate=Integer.parseInt(getDate().substring(0,2));

                int iHour=Integer.parseInt(hour.substring(0,2));
                int iMinute=Integer.parseInt(hour.substring(3,5));

                int cHour=Integer.parseInt(getCurrentTime().substring(0,2));
                int cMinute=Integer.parseInt(getCurrentTime().substring(3,5));

                int tempMinute=Math.abs(cMinute-iMinute);
                if(iDate==cDate){
                    if(tempMinute<50){
                        login();
                    }else{
                        Toast.makeText(LoginActivity.this,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                login();
            }
        }
    }
}

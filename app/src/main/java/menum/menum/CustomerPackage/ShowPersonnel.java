package menum.menum.CustomerPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerAdapterPackage.ShowPersonnelAdapter;
import menum.menum.Model.AddPersonnelPost;
import menum.menum.R;

public class ShowPersonnel extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recPersonnel;
    private ShowPersonnelAdapter showPersonnelAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog progressDialog;

    private static String phoneNumber;
    private List<AddPersonnelPost> addPersonnelPostList;

    private DatabaseReference databaseReference;

    private static final String baseUrlHour="https://www.timeanddate.com/";

    private PermissionManager permissionManager;

    public ShowPersonnel(){

    }

    public ShowPersonnel(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShowPersonnel.this,CustomerMainScreen.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_personnel);
        init();
        new GetTimeFromNet(ShowPersonnel.this).execute();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowPersonnel.this,CustomerMainScreen.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        recPersonnel=findViewById(R.id.recPersonnel);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recPersonnel.setLayoutManager(linearLayoutManager);
        addPersonnelPostList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor..");
        progressDialog.show();

        permissionManager=new PermissionManager() {
            @Override
            public boolean checkAndRequestPermissions(Activity activity) {
                return super.checkAndRequestPermissions(activity);
            }
        };
    }

    private void getPersonnel(final boolean currentDate) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.PERSONNELS)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PERSONNELS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapUID:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.PERSONNELS)
                                                        .child(snapUID.getKey().toString())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                progressDialog.dismiss();
                                                                AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                                                addPersonnelPostList.add(addPersonnelPost);
                                                                showPersonnelAdapter=new ShowPersonnelAdapter(ShowPersonnel.this,phoneNumber,addPersonnelPostList,currentDate);
                                                                recPersonnel.setAdapter(showPersonnelAdapter);
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
                            progressDialog.dismiss();
                            Toast.makeText(ShowPersonnel.this,"Personel Bilgisi Bulunamadı",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
        String hour;
        String date;
        private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        public GetTimeFromNet(Context context) {
            this.context=context;
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
                if(iDate==cDate){
                    getPersonnel(true);
                }else{
                    progressDialog.dismiss();
                    getPersonnel(false);
                    Toast.makeText(context,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                getPersonnel(true);
            }
        }
    }
}

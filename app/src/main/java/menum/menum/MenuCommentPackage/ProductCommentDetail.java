package menum.menum.MenuCommentPackage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.Model.CommentPost;
import menum.menum.R;

public class ProductCommentDetail extends AppCompatActivity {

    private TextView tvMenuCategoryName,tvProductName;
    private Button btnBack,btnSend;
    private RecyclerView recComments;
    private EditText etComment;
    private LinearLayoutManager linearLayoutManager;
    private ProductCommentDetailAdapter commentDetailAdapter;

    private static String phoneNumber;
    private static String menuCategoryName;
    private static String productName;

    private List<CommentPost> commentPostList;
    private DatabaseReference databaseReference;

    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    public ProductCommentDetail(){

    }

    public ProductCommentDetail(String phoneNumber, String menuCategoryName, String productName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new MenuCommentProducts(phoneNumber,menuCategoryName);
        startActivity(new Intent(ProductCommentDetail.this,MenuCommentProducts.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comment_detail);
        init();
        getComments();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MenuCommentProducts(phoneNumber,menuCategoryName);
                startActivity(new Intent(ProductCommentDetail.this,MenuCommentProducts.class));
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etComment.getText().toString())){
                    etComment.setError("Yorum Giriniz");
                    return;
                }
                new GetTimeFromNet(ProductCommentDetail.this,etComment.getText().toString()).execute();
            }
        });
    }

    private void init() {
        tvMenuCategoryName=findViewById(R.id.tvMenuCategoryName);
        tvMenuCategoryName.setText(menuCategoryName);
        tvProductName=findViewById(R.id.tvProductName);
        tvProductName.setText(productName);
        btnBack=findViewById(R.id.btnBack);
        etComment=findViewById(R.id.etComment);
        btnSend=findViewById(R.id.btnSend);
        recComments=findViewById(R.id.recComments);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recComments.setLayoutManager(linearLayoutManager);
        commentPostList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void getComments() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PRODUCT_COMMENTS)
                .child(menuCategoryName)
                .child(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapUID:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                    .child(menuCategoryName)
                                    .child(productName)
                                    .child(snapUID.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            CommentPost commentPost=dataSnapshot.getValue(CommentPost.class);
                                            commentPostList.add(commentPost);
                                            commentDetailAdapter=new ProductCommentDetailAdapter(
                                                    ProductCommentDetail.this,
                                                    phoneNumber,
                                                    menuCategoryName,
                                                    productName,
                                                    commentPostList,
                                                    getImeiNumber());
                                            recComments.setAdapter(commentDetailAdapter);
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
    }

    public String getImeiNumber(){
        int permissionCheck= ContextCompat.checkSelfPermission(ProductCommentDetail.this, Manifest.permission.READ_PHONE_STATE);
        TelephonyManager telephonyManager = (TelephonyManager) ProductCommentDetail.this.getSystemService(TELEPHONY_SERVICE);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) ProductCommentDetail.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            return "";
        }else{
            String deviceIMEI = telephonyManager.getDeviceId();
            return deviceIMEI;
        }
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
        String comment ;
        String UID=String.valueOf(UUID.randomUUID());
        public GetTimeFromNet(ProductCommentDetail productCommentDetail, String comment) {
            this.comment=comment;
            this.context=productCommentDetail;
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
                        CommentPost commentPost=new CommentPost();
                        commentPost.setDate(getCurrentDate());
                        commentPost.setHour(getCurrentTime());
                        commentPost.setComment(comment);
                        commentPost.setUID(UID);
                        commentPost.setName("Yetkili");
                        commentPost.setSurname("");
                        commentPost.setImeiNumber(getImeiNumber());
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.PRODUCT_COMMENTS)
                                .child(menuCategoryName)
                                .child(productName)
                                .child(UID)
                                .setValue(commentPost);

                        context.startActivity(new Intent(context,ProductCommentDetail.class));
                        Toast.makeText(context,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                CommentPost commentPost=new CommentPost();
                commentPost.setDate(getCurrentDate());
                commentPost.setHour(getCurrentTime());
                commentPost.setComment(comment);
                commentPost.setUID(UID);
                commentPost.setName("Yetkili");
                commentPost.setSurname("");
                commentPost.setImeiNumber(getImeiNumber());
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(UID)
                        .setValue(commentPost);

                context.startActivity(new Intent(context,ProductCommentDetail.class));
                Toast.makeText(context,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

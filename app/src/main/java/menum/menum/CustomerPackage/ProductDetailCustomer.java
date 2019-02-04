package menum.menum.CustomerPackage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import menum.menum.CustomerAdapterPackage.CommentAdapter;
import menum.menum.MainScreenPackage.AddProduct;
import menum.menum.MainScreenPackage.ProductDetail;
import menum.menum.MainScreenPackage.Products;
import menum.menum.Model.AddProductPost;
import menum.menum.Model.AdviceComplaintPost;
import menum.menum.Model.CommentPost;
import menum.menum.Model.LikeDislikePost;
import menum.menum.Model.NewNotificationPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.R;
import menum.menum.RoomDatabase.AbstractPackage.CustomerAppDatabase;
import menum.menum.RoomDatabase.Model.Customer;

public class ProductDetailCustomer extends AppCompatActivity {

    private ImageView productImage;
    private TextView tvMenuTitle,tvProductName,tvPrice,tvTimeService,tvProductDetail;
    private TextView tvUnLike,tvUnLikeCounter,tvLike,tvLikeCounter;
    private Button btnBack;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recComments;
    private CommentAdapter commentAdapter;
    private CommentPost commentPost;
    private List<CommentPost> commentPostList;
    private List<Customer> customerList;

    private EditText etComment;
    private Button btnSend;

    private static String phoneNumber ;
    private static String menuCategoryName ;
    private static String productName;

    private DatabaseReference databaseReference;
    private static boolean isOpen;

    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    public ProductDetailCustomer(String phoneNumber, String menuCategoryName, String productName){
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName = productName;
    }

    public ProductDetailCustomer() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new ProductsCustomer(phoneNumber,menuCategoryName);
        startActivity(new Intent(ProductDetailCustomer.this,ProductsCustomer.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_customer);
        init();
        setProductProperty();
        showLikeDislikeOfProduct();
        showLikeDislikeCounter();
        likeDislike();
        getComments();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProductsCustomer(phoneNumber,menuCategoryName);
                startActivity(new Intent(ProductDetailCustomer.this,ProductsCustomer.class));
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etComment.getText().toString())){
                    Toast.makeText(ProductDetailCustomer.this,"Yorum Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                sendComment();
            }
        });
    }

    private void init() {
        productImage=findViewById(R.id.productImage);
        tvMenuTitle=findViewById(R.id.tvMenuTitle);
        tvProductName=findViewById(R.id.tvProductName);
        tvPrice=findViewById(R.id.tvPrice);
        tvTimeService=findViewById(R.id.tvTimeService);
        tvProductDetail=findViewById(R.id.tvProductDetail);
        btnBack=findViewById(R.id.btnBack);
        tvUnLike=findViewById(R.id.tvUnLike);
        tvUnLike.setTag(R.drawable.ic_thumb_down_white_24dp);
        tvUnLikeCounter=findViewById(R.id.tvUnLikeCounter);
        tvLike=findViewById(R.id.tvLike);
        tvLike.setTag(R.drawable.ic_thumb_up_white_24dp);
        tvLikeCounter=findViewById(R.id.tvLikeCounter);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        recComments=findViewById(R.id.recComments);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recComments.setLayoutManager(linearLayoutManager);
        commentPostList=new ArrayList<>();

        btnSend=findViewById(R.id.btnSend);
        etComment=findViewById(R.id.etComment);
    }

    private void getComments() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PRODUCT_COMMENTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(menuCategoryName)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                    .child(menuCategoryName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(productName)){
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
                                                                                    commentAdapter=new CommentAdapter(
                                                                                            ProductDetailCustomer.this,
                                                                                            phoneNumber,
                                                                                            menuCategoryName,
                                                                                            productName,
                                                                                            getImeiNumber(),
                                                                                            commentPostList);
                                                                                    recComments.setAdapter(commentAdapter);
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

    private void setProductProperty() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MENU_CATEGORY)
                .child(menuCategoryName)
                .child(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                        if(addProductPost.getProductImageUrl().equalsIgnoreCase("")==false){
                            Glide.with(ProductDetailCustomer.this).load(addProductPost.getProductImageUrl()).into(productImage);
                        }
                        tvProductName.setText(addProductPost.getProductName());
                        tvPrice.setText(addProductPost.getProductPrice());
                        tvMenuTitle.setText(menuCategoryName);
                        tvProductDetail.setText(addProductPost.getProductExplanation());
                        tvTimeService.setText(addProductPost.getProductServiceTime());

                        if(addProductPost.getCommentSituation().equalsIgnoreCase("false")){
                            recComments.setVisibility(View.INVISIBLE);
                            btnSend.setVisibility(View.INVISIBLE);
                            etComment.setHint("Yoruma Kapalı");
                            etComment.setEnabled(false);
                        }else{
                            recComments.setVisibility(View.VISIBLE);
                            btnSend.setVisibility(View.VISIBLE);
                            etComment.setEnabled(true);
                            etComment.setHint("Yorum Ekle");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showLikeDislikeOfProduct() {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // eğer müşterinin customer bölümünde imei numarası kaydı var ise buraya gir
                        if(dataSnapshot.hasChild(getImeiNumber())){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // eğer müşterinin customer bölümünde likeDislike kaydı var ise buraya gir
                                            if(dataSnapshot.hasChild(MenumConstant.LIKE_DISLIKE)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(getImeiNumber())
                                                        .child(MenumConstant.LIKE_DISLIKE)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                // eğer müşterinin customer bölümünde işletmenin cep numarası kaydı var ise buraya gir
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(getImeiNumber())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    // eğer müşterinin customer bölümünde bu menucategori kaydı var ise buraya gir
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(getImeiNumber())
                                                                                                .child(MenumConstant.LIKE_DISLIKE)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName)
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        // eğer müşterinin customer bölümünde bu ürüne ait kaydı var ise buraya gir
                                                                                                        if(dataSnapshot.hasChild(productName)){
                                                                                                            databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(getImeiNumber())
                                                                                                                    .child(MenumConstant.LIKE_DISLIKE)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(productName)
                                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                        @Override
                                                                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                            LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                                                                                            String likeDislike=likeDislikePost.getLikeDislike();
                                                                                                                            if(likeDislike.equalsIgnoreCase("true")){
                                                                                                                                tvLike.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp);
                                                                                                                                tvLike.setTag(R.drawable.ic_thumb_up_black_24dp);
                                                                                                                            }else{
                                                                                                                                tvUnLike.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp);
                                                                                                                                tvUnLike.setTag(R.drawable.ic_thumb_down_black_24dp);
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
    }

    private void showLikeDislikeCounter() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MENU_LIKE)
                .child(menuCategoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(productName)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_LIKE)
                                    .child(menuCategoryName)
                                    .child(productName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                            tvUnLikeCounter.setText(likeDislikePost.getDislikeCounter());
                                            tvLikeCounter.setText(likeDislikePost.getLikeCounter());
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

    private void likeDislike() {
        tvLike.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if(tvLike.getTag().toString().equalsIgnoreCase(String.valueOf(R.drawable.ic_thumb_up_black_24dp))){
                    tvLike.setBackgroundResource(R.drawable.ic_thumb_up_white_24dp);
                    tvLike.setTag(R.drawable.ic_thumb_up_white_24dp);
                    removeLikeDislike();
                    decrementLikeDislike(true);
                }else{
                    NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                    notificationCounterPost.setNewCounter("1");
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.LIKE_NOTIFICATION)
                            .child("newCounter")
                            .setValue(notificationCounterPost.getNewCounter());

                    NewNotificationPost newNotificationPost=new NewNotificationPost();
                    newNotificationPost.setDate(getCurrentDate());
                    newNotificationPost.setHour(getCurrentTime());
                    newNotificationPost.setNotification(menuCategoryName + " kategorisindeki " + productName + " adlı ürününüz yeni bir beğeni aldı.");
                    newNotificationPost.setSeen("false");
                    newNotificationPost.setProductName(productName);
                    newNotificationPost.setMenuCategoryName(menuCategoryName);
                    newNotificationPost.setCodeNot("3");
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION)
                            .child(getCurrentDate())
                            .child(getCurrentTime())
                            .setValue(newNotificationPost);

                    Toast.makeText(ProductDetailCustomer.this,"Ürün beğenilenler arasına eklendi",Toast.LENGTH_SHORT).show();
                    tvLike.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp);
                    tvLike.setTag(R.drawable.ic_thumb_up_black_24dp);
                    setProductLikeDislike("true");
                    incrementLikeDislike(true);
                }
            }
        });

        tvUnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvUnLike.getTag().toString().equalsIgnoreCase(String.valueOf(R.drawable.ic_thumb_down_black_24dp))){
                    tvUnLike.setBackgroundResource(R.drawable.ic_thumb_down_white_24dp);
                    tvUnLike.setTag(R.drawable.ic_thumb_down_white_24dp);
                    removeLikeDislike();
                    decrementLikeDislike(false);
                }else{
                    tvUnLike.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp);
                    tvUnLike.setTag(R.drawable.ic_thumb_down_black_24dp);
                    setProductLikeDislike("false");
                    incrementLikeDislike(false);
                }
            }
        });
    }

    private void setProductLikeDislike(String likeDislike) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.LIKE_DISLIKE)
                .child(phoneNumber)
                .child(menuCategoryName)
                .child(productName)
                .child("likeDislike")
                .setValue(likeDislike);
    }

    private void removeLikeDislike() {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.LIKE_DISLIKE)
                .child(phoneNumber)
                .child(menuCategoryName)
                .child(productName)
                .removeValue();
    }

    private void incrementLikeDislike(boolean likeDislike) {
        if(likeDislike){
            // like arttırma bölümü
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_LIKE)
                    .child(menuCategoryName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(productName)){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                int likeCounter=Integer.parseInt(likeDislikePost.getLikeCounter());
                                                likeCounter=likeCounter+1;
                                                tvLikeCounter.setText(Integer.toString(likeCounter));
                                                likeDislikePost.setLikeCounter(Integer.toString(likeCounter));
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("likeCounter")
                                                        .setValue(likeDislikePost.getLikeCounter());
                                                // eğer müşteri ürünü önce begenmedi yapıp sonra begendiyse bu if koşulu çalışır
                                                if(tvUnLike.getTag().toString().equalsIgnoreCase(String.valueOf(R.drawable.ic_thumb_down_black_24dp))){
                                                    tvUnLike.setBackgroundResource(R.drawable.ic_thumb_down_white_24dp);
                                                    tvUnLike.setTag(R.drawable.ic_thumb_down_white_24dp);
                                                    decrementLikeDislike(false);
                                                }
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_CATEGORY)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                                                databaseReference
                                                                        .child(MenumConstant.STORE)
                                                                        .child(phoneNumber)
                                                                        .child(MenumConstant.MENU_LIKE)
                                                                        .child(menuCategoryName)
                                                                        .child(productName)
                                                                        .child("productImageUrl")
                                                                        .setValue(addProductPost.getProductImageUrl());
                                                                databaseReference
                                                                        .child(MenumConstant.STORE)
                                                                        .child(phoneNumber)
                                                                        .child(MenumConstant.MENU_LIKE)
                                                                        .child(menuCategoryName)
                                                                        .child(productName)
                                                                        .child("salesSituation")
                                                                        .setValue(addProductPost.getSalesSituation());
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }else{
                                LikeDislikePost likeDislikePost=new LikeDislikePost();
                                likeDislikePost.setDislikeCounter("0");
                                likeDislikePost.setLikeCounter("1");
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .setValue(likeDislikePost);
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_CATEGORY)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("productImageUrl")
                                                        .setValue(addProductPost.getProductImageUrl());
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("salesSituation")
                                                        .setValue(addProductPost.getSalesSituation());
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
            // dislike arttırma bölümü
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_LIKE)
                    .child(menuCategoryName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(productName)){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                int dislikeCounter=Integer.parseInt(likeDislikePost.getDislikeCounter());
                                                dislikeCounter=dislikeCounter+1;
                                                tvUnLikeCounter.setText(Integer.toString(dislikeCounter));
                                                likeDislikePost.setDislikeCounter(Integer.toString(dislikeCounter));
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("dislikeCounter")
                                                        .setValue(likeDislikePost.getDislikeCounter());
                                                // adam ürünü önce beğendi yaptı sonradan beğenmedi yaptı ise bu koşul çalışır
                                                if(tvLike.getTag().toString().equalsIgnoreCase(String.valueOf(R.drawable.ic_thumb_up_black_24dp))){
                                                    tvLike.setBackgroundResource(R.drawable.ic_thumb_up_white_24dp);
                                                    tvLike.setTag(R.drawable.ic_thumb_up_white_24dp);
                                                    decrementLikeDislike(true);
                                                }
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_CATEGORY)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                                                databaseReference
                                                                        .child(MenumConstant.STORE)
                                                                        .child(phoneNumber)
                                                                        .child(MenumConstant.MENU_LIKE)
                                                                        .child(menuCategoryName)
                                                                        .child(productName)
                                                                        .child("productImageUrl")
                                                                        .setValue(addProductPost.getProductImageUrl());
                                                                databaseReference
                                                                        .child(MenumConstant.STORE)
                                                                        .child(phoneNumber)
                                                                        .child(MenumConstant.MENU_LIKE)
                                                                        .child(menuCategoryName)
                                                                        .child(productName)
                                                                        .child("salesSituation")
                                                                        .setValue(addProductPost.getSalesSituation());
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }else{
                                LikeDislikePost likeDislikePost=new LikeDislikePost();
                                likeDislikePost.setDislikeCounter("1");
                                likeDislikePost.setLikeCounter("0");
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .setValue(likeDislikePost);
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_CATEGORY)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("productImageUrl")
                                                        .setValue(addProductPost.getProductImageUrl());
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("salesSituation")
                                                        .setValue(addProductPost.getSalesSituation());
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
    }

    private void decrementLikeDislike(boolean likeDislike) {
        if(likeDislike){
            // like azaltma bölümü
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_LIKE)
                    .child(menuCategoryName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(productName)){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                int likeCounter=Integer.parseInt(likeDislikePost.getLikeCounter());
                                                likeCounter=likeCounter-1;
                                                tvLikeCounter.setText(Integer.toString(likeCounter));
                                                likeDislikePost.setLikeCounter(Integer.toString(likeCounter));
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("likeCounter")
                                                        .setValue(likeDislikePost.getLikeCounter());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }else{
                                LikeDislikePost likeDislikePost=new LikeDislikePost();
                                likeDislikePost.setDislikeCounter("0");
                                likeDislikePost.setLikeCounter("1");
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .setValue(likeDislikePost);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }else{
            // dislike azaltma bölümü
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_LIKE)
                    .child(menuCategoryName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(productName)){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                int dislikeCounter=Integer.parseInt(likeDislikePost.getDislikeCounter());
                                                dislikeCounter=dislikeCounter-1;
                                                tvUnLikeCounter.setText(Integer.toString(dislikeCounter));
                                                likeDislikePost.setDislikeCounter(Integer.toString(dislikeCounter));
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .child(productName)
                                                        .child("dislikeCounter")
                                                        .setValue(likeDislikePost.getDislikeCounter());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }else{
                                LikeDislikePost likeDislikePost=new LikeDislikePost();
                                likeDislikePost.setDislikeCounter("1");
                                likeDislikePost.setLikeCounter("0");
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_LIKE)
                                        .child(menuCategoryName)
                                        .child(productName)
                                        .setValue(likeDislikePost);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void sendComment() {
        new GetTimeFromNet(etComment.getText().toString()).execute();
    }

    public String getImeiNumber(){
        int permissionCheck= ContextCompat.checkSelfPermission(ProductDetailCustomer.this, Manifest.permission.READ_PHONE_STATE);
        TelephonyManager telephonyManager = (TelephonyManager) ProductDetailCustomer.this.getSystemService(TELEPHONY_SERVICE);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) ProductDetailCustomer.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
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
        public GetTimeFromNet(String comment) {
            this.comment=comment;
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
                        commentPost.setName(getCustomerName());
                        commentPost.setSurname(getCustomerSurName());
                        commentPost.setImeiNumber(getImeiNumber());
                        databaseReference
                                .child(MenumConstant.CUSTOMERS)
                                .child(getImeiNumber())
                                .child(MenumConstant.PRODUCT_COMMENTS)
                                .child(phoneNumber)
                                .child(menuCategoryName)
                                .child(productName)
                                .child(UID)
                                .setValue(commentPost);
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.PRODUCT_COMMENTS)
                                .child(menuCategoryName)
                                .child(productName)
                                .child(UID)
                                .setValue(commentPost);
                        NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                        notificationCounterPost.setNewCounter("1");
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.NOTIFICATIONS)
                                .child(MenumConstant.COMMENT_NOTIFICATION)
                                .child("newCounter")
                                .setValue(notificationCounterPost.getNewCounter());

                        NewNotificationPost newNotificationPost=new NewNotificationPost();
                        newNotificationPost.setDate(getCurrentDate());
                        newNotificationPost.setHour(getCurrentTime());
                        newNotificationPost.setNotification(menuCategoryName + " kategorisindeki " + productName + " adlı ürününüze yeni bir yorum geldi.");
                        newNotificationPost.setSeen("false");
                        newNotificationPost.setProductName(productName);
                        newNotificationPost.setMenuCategoryName(menuCategoryName);
                        newNotificationPost.setCodeNot("3");
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.NOTIFICATIONS)
                                .child(MenumConstant.NEW_NOTIFICATION)
                                .child(getCurrentDate())
                                .child(getCurrentTime())
                                .setValue(newNotificationPost);
                        startActivity(getIntent());
                        Toast.makeText(ProductDetailCustomer.this,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ProductDetailCustomer.this,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ProductDetailCustomer.this,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                CommentPost commentPost=new CommentPost();
                commentPost.setDate(getCurrentDate());
                commentPost.setHour(getCurrentTime());
                commentPost.setComment(comment);
                commentPost.setUID(UID);
                commentPost.setName(getCustomerName());
                commentPost.setSurname(getCustomerSurName());
                commentPost.setImeiNumber(getImeiNumber());
                databaseReference
                        .child(MenumConstant.CUSTOMERS)
                        .child(getImeiNumber())
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(phoneNumber)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(UID)
                        .setValue(commentPost);
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(UID)
                        .setValue(commentPost);
                NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                notificationCounterPost.setNewCounter("1");
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.COMMENT_NOTIFICATION)
                        .child("newCounter")
                        .setValue(notificationCounterPost.getNewCounter());

                NewNotificationPost newNotificationPost=new NewNotificationPost();
                newNotificationPost.setDate(getCurrentDate());
                newNotificationPost.setHour(getCurrentTime());
                newNotificationPost.setNotification(menuCategoryName + " kategorisindeki " + productName + " adlı ürününüze yeni bir yorum geldi.");
                newNotificationPost.setSeen("false");
                newNotificationPost.setProductName(productName);
                newNotificationPost.setMenuCategoryName(menuCategoryName);
                newNotificationPost.setCodeNot("3");
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION)
                        .child(getCurrentDate())
                        .child(getCurrentTime())
                        .setValue(newNotificationPost);
                startActivity(getIntent());
                Toast.makeText(ProductDetailCustomer.this,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getCustomerName(){
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(ProductDetailCustomer.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        return customerList.get(0).getName();
    }

    public String getCustomerSurName(){
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(ProductDetailCustomer.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        return customerList.get(0).getSurname();
    }
}

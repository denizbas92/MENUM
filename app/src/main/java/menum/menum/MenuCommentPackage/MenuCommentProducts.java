package menum.menum.MenuCommentPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerAdapterPackage.ProductsCustomerAdapter;
import menum.menum.CustomerPackage.MenuCategoryCustomer;
import menum.menum.CustomerPackage.ProductsCustomer;
import menum.menum.Model.AddProductPost;
import menum.menum.Model.LikeDislikePost;
import menum.menum.R;

public class MenuCommentProducts extends AppCompatActivity {
    private static Context context;
    private Button btnBack;
    private RecyclerView recProducts;
    private GridLayoutManager gridLayoutManager;
    private MenuCommentProductAdapter menuCommentProductAdapter;
    private TextView tvMenuCategoryName;

    private static String phoneNumber;
    private static String menuCategoryName;

    private DatabaseReference databaseReference;
    private List<LikeDislikePost> likeDislikePostList;
    private List<String> listProductName;
    private ProgressDialog progressDialog;

    public MenuCommentProducts(){

    }

    public MenuCommentProducts(String phoneNumber, String menuCategoryName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new MenuCommentCategory(phoneNumber);
        startActivity(new Intent(MenuCommentProducts.this, MenuCommentCategory.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_comment_products);
        init();
        getProducts();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MenuCommentCategory(phoneNumber);
                startActivity(new Intent(MenuCommentProducts.this,MenuCommentCategory.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        recProducts=findViewById(R.id.recProducts);
        tvMenuCategoryName=findViewById(R.id.tvMenuCategoryName);
        tvMenuCategoryName.setText(menuCategoryName);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        likeDislikePostList=new ArrayList<>();
        listProductName=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.show();
    }

    private void getProducts() {

        likeDislikePostList.clear();
        final DisplayMetrics metrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth=metrics.widthPixels;
        int screenHeight=metrics.heightPixels;
        double wi=(double)screenWidth/(double)metrics.xdpi;
        double hi=(double)screenHeight/(double)metrics.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        final double screenInches = Math.sqrt(x+y);

        if (screenInches<5){
            gridLayoutManager=new GridLayoutManager(this,1);
        }else if(screenInches>5 && screenInches<7){
            gridLayoutManager=new GridLayoutManager(this,2);
        }else{
            gridLayoutManager=new GridLayoutManager(this,3);
        }

        recProducts.setLayoutManager(gridLayoutManager);

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.MENU_LIKE)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_LIKE)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(menuCategoryName)){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(final DataSnapshot snapProduct:dataSnapshot.getChildren()){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.MENU_LIKE)
                                                                            .child(menuCategoryName)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(snapProduct.getKey().toString())){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.STORE)
                                                                                                .child(phoneNumber)
                                                                                                .child(MenumConstant.MENU_LIKE)
                                                                                                .child(menuCategoryName)
                                                                                                .child(snapProduct.getKey().toString())
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        progressDialog.dismiss();
                                                                                                        LikeDislikePost likeDislikePost=dataSnapshot.getValue(LikeDislikePost.class);
                                                                                                        likeDislikePostList.add(likeDislikePost);
                                                                                                        listProductName.add(snapProduct.getKey().toString());
                                                                                                        menuCommentProductAdapter=new MenuCommentProductAdapter(MenuCommentProducts.this,phoneNumber,menuCategoryName,listProductName,likeDislikePostList);
                                                                                                        recProducts.setAdapter(menuCommentProductAdapter);
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                                    }
                                                                                                });
                                                                                    }else{
                                                                                        progressDialog.dismiss();
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
                                                progressDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

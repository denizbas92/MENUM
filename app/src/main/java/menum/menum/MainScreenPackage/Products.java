package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
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
import menum.menum.Model.AddProductPost;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.ProductAdapter;

public class Products extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recProducts;
    private GridLayoutManager gridLayoutManager;
    private ProductAdapter productAdapter;
    private Button btnAdd;
    private TextView tvMenuCategoryName;

    private static String phoneNumber;
    private static String menuCategoryName;

    private DatabaseReference databaseReference;
    private List<String> listProducts;
    private List<AddProductPost> addProductPostList;

    public Products(){

    }

    public Products(String phoneNumber,String menuCategoryName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Products.this,MenuCategory.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        init();
        getProducts();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProducts();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Products.this,MenuCategory.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        btnAdd=findViewById(R.id.btnAdd);
        recProducts=findViewById(R.id.recProducts);
        tvMenuCategoryName=findViewById(R.id.tvMenuCategoryName);
        tvMenuCategoryName.setText(menuCategoryName);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        listProducts=new ArrayList<>();
        addProductPostList=new ArrayList<>();
    }

    private void getProducts() {
        addProductPostList.clear();
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
                .child(MenumConstant.MENU_CATEGORY)
                .child(menuCategoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapProduct:dataSnapshot.getChildren()){
                            if(snapProduct.getKey().toString().equalsIgnoreCase(menuCategoryName)==false){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_CATEGORY)
                                        .child(menuCategoryName)
                                        .child(snapProduct.getKey().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try{
                                                    AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                                    addProductPostList.add(addProductPost);
                                                    productAdapter=new ProductAdapter(Products.this,phoneNumber,menuCategoryName,addProductPostList);
                                                    recProducts.setAdapter(productAdapter);
                                                }catch (Exception e){

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addProducts() {
        new AddProduct(phoneNumber,menuCategoryName);
        startActivity(new Intent(Products.this,AddProduct.class));
        finish();
    }
}

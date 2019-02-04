package menum.menum.MenuCommentPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerAdapterPackage.MenuCategoryCustomerAdapter;
import menum.menum.CustomerPackage.CustomerMainScreen;
import menum.menum.CustomerPackage.MenuCategoryCustomer;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.R;

public class MenuCommentCategory extends AppCompatActivity {
    private Button btnBack;
    private RecyclerView recMenuCategory;
    private GridLayoutManager gridLayoutManager;
    private MenuCategoryCustomerAdapter menuCategoryAdapter;

    private static String phoneNumber;

    private DatabaseReference databaseReference;
    private List<String> listMenuCategory;
    private ProgressDialog progressDialog;

    public MenuCommentCategory(){

    }

    public MenuCommentCategory(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MenuCommentCategory.this,MainScreen.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_comment_category);
        init();
        getMenuCategory();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomerMainScreen(phoneNumber);
                startActivity(new Intent(MenuCommentCategory.this,MainScreen.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        recMenuCategory=findViewById(R.id.recMenuCategory);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        listMenuCategory=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.show();
    }

    private void getMenuCategory() {
        listMenuCategory.clear();
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
            gridLayoutManager=new GridLayoutManager(this,2);
        }else if(screenInches>5 && screenInches<7){
            gridLayoutManager=new GridLayoutManager(this,2);
        }else{
            gridLayoutManager=new GridLayoutManager(this,3);
        }

        recMenuCategory.setLayoutManager(gridLayoutManager);
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MENU_CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        for(DataSnapshot snapMenuCategory:dataSnapshot.getChildren()){
                            listMenuCategory.add(snapMenuCategory.getKey().toString());
                        }
                        menuCategoryAdapter=new MenuCategoryCustomerAdapter(MenuCommentCategory.this,phoneNumber,listMenuCategory);
                        recMenuCategory.setAdapter(menuCategoryAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

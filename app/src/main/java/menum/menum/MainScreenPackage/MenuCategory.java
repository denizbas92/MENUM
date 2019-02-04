package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.MenuCategoryAdapter;

public class MenuCategory extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recMenuCategory;
    private GridLayoutManager gridLayoutManager;
    private MenuCategoryAdapter menuCategoryAdapter;
    private Button btnAdd;

    private static String phoneNumber;

    private DatabaseReference databaseReference;
    private List<String> listMenuCategory;

    public MenuCategory(){

    }

    public MenuCategory(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MenuCategory.this,MainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_category);
        init();
        getMenuCategory();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMenuCategory();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuCategory.this,MainScreen.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        btnAdd=findViewById(R.id.btnAdd);
        recMenuCategory=findViewById(R.id.recMenuCategory);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        listMenuCategory=new ArrayList<>();
    }

    private void addMenuCategory() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MenuCategory.this);
        View view= LayoutInflater.from(MenuCategory.this).inflate(R.layout.layout_add_menu_category,null);
        final EditText etMenuCategory=view.findViewById(R.id.etMenuCategory);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        Button btnAdd=view.findViewById(R.id.btnAdd);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String menuCategoryName=etMenuCategory.getText().toString();
                if(TextUtils.isEmpty(menuCategoryName)){
                    Snackbar.make(view, "Kategori Adı Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.MENU_CATEGORY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapMenuCategory:dataSnapshot.getChildren()){
                                    listMenuCategory.add(snapMenuCategory.getKey().toString());
                                }
                                if(listMenuCategory.contains(menuCategoryName)){
                                    Snackbar.make(view, "Eklenmiş Kategori", Snackbar.LENGTH_LONG).show();
                                }else{
                                    databaseReference
                                            .child(MenumConstant.STORE)
                                            .child(phoneNumber)
                                            .child(MenumConstant.MENU_CATEGORY)
                                            .child(menuCategoryName)
                                            .child(menuCategoryName)
                                            .setValue(menuCategoryName);
                                    dialog.dismiss();
                                    startActivity(getIntent());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
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
                        for(DataSnapshot snapMenuCategory:dataSnapshot.getChildren()){
                            listMenuCategory.add(snapMenuCategory.getKey().toString());
                        }
                        menuCategoryAdapter=new MenuCategoryAdapter(MenuCategory.this,phoneNumber,listMenuCategory);
                        recMenuCategory.setAdapter(menuCategoryAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

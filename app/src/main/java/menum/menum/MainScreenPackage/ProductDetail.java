package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.Model.AddProductPost;
import menum.menum.R;

public class ProductDetail extends AppCompatActivity {

    private ImageView productImage;
    private TextView tvMenuTitle,tvProductName,tvPrice,tvTimeService,tvProductDetail;
    private Button btnBack;

    private RadioButton rbOpen,rbClose;
    private RadioButton rbOpenComment,rbCloseComment;

    private static String phoneNumber ;
    private static String menuCategoryName ;
    private static String productName;

    private DatabaseReference databaseReference;
    private static boolean isOpen;
    private static boolean isOpenComment;

    public ProductDetail(String phoneNumber, String menuCategoryName, String productName){
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName = productName;
    }

    public ProductDetail() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProductDetail.this,Products.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        init();
        setProductProperty();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetail.this,Products.class));
                finish();
            }
        });

        rbOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbOpen.setTextSize(18);
                rbClose.setTextSize(14);
                rbOpen.setChecked(true);
                rbOpen.setTypeface(Typeface.DEFAULT_BOLD);
                rbClose.setTypeface(Typeface.DEFAULT);
                rbClose.setChecked(false);
                confirmProcessSell(1);
            }
        });

        rbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbOpen.setTextSize(14);
                rbClose.setTextSize(18);
                rbClose.setChecked(true);
                rbClose.setTypeface(Typeface.DEFAULT_BOLD);
                rbOpen.setTypeface(Typeface.DEFAULT);
                rbOpen.setChecked(false);
                confirmProcessSell(2);
            }
        });

        rbOpenComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbOpenComment.setTextSize(18);
                rbCloseComment.setTextSize(14);
                rbOpenComment.setChecked(true);
                rbOpenComment.setTypeface(Typeface.DEFAULT_BOLD);
                rbCloseComment.setTypeface(Typeface.DEFAULT);
                rbCloseComment.setChecked(false);
                confirmProcessComment(1);
            }
        });

        rbCloseComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbOpenComment.setTextSize(14);
                rbCloseComment.setTextSize(18);
                rbCloseComment.setChecked(true);
                rbCloseComment.setTypeface(Typeface.DEFAULT_BOLD);
                rbOpenComment.setTypeface(Typeface.DEFAULT);
                rbOpenComment.setChecked(false);
                confirmProcessComment(2);
            }
        });
    }

    private void confirmProcessComment(final int ID) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(ProductDetail.this);
        View viewConfirm= LayoutInflater.from(ProductDetail.this).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
        Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
        if(ID==1){
            tvTitle.setText("Ürün yoruma açık pozisyona getirilecek. Bunu onaylıyor musunuz ?");
        }else{
            tvTitle.setText("Ürün yoruma kapalı pozisyona getirilecek. Bunu onaylıyor musunuz ?");
        }
        builder.setView(viewConfirm);
        final android.app.AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    rbOpen.setTextSize(18);
                    rbClose.setTextSize(14);
                    rbOpen.setTypeface(Typeface.DEFAULT_BOLD);
                    rbClose.setTypeface(Typeface.DEFAULT);
                    rbOpen.setChecked(true);
                    rbClose.setChecked(false);
                }else{
                    rbOpen.setTextSize(14);
                    rbClose.setTextSize(18);
                    rbClose.setTypeface(Typeface.DEFAULT_BOLD);
                    rbOpen.setTypeface(Typeface.DEFAULT);
                    rbClose.setChecked(true);
                    rbOpen.setChecked(false);
                }
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ID==1){
                    databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY)
                            .child(menuCategoryName)
                            .child(productName)
                            .child("commentSituation")
                            .setValue("true");
                    Toast.makeText(ProductDetail.this,"Ürün yoruma açık pozisyona getirildi",Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY)
                            .child(menuCategoryName)
                            .child(productName)
                            .child("commentSituation")
                            .setValue("false");
                    Toast.makeText(ProductDetail.this,"Ürün yoruma kapalı pozisyona getirildi",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    private void confirmProcessSell(final int ID) {
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(ProductDetail.this);
            View viewConfirm= LayoutInflater.from(ProductDetail.this).inflate(R.layout.layout_confirm_dialog,null);
            TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
            Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
            Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
            if(ID==1){
                tvTitle.setText("Ürün satışa açık pozisyona getirilecek. Bunu onaylıyor musunuz ?");
            }else{
                tvTitle.setText("Ürün satışa kapalı pozisyona getirilecek. Bunu onaylıyor musunuz ?");
            }
            builder.setView(viewConfirm);
            final android.app.AlertDialog dialog=builder.create();
            dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
            dialog.show();

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isOpen){
                        rbOpen.setTextSize(18);
                        rbClose.setTextSize(14);
                        rbOpen.setTypeface(Typeface.DEFAULT_BOLD);
                        rbClose.setTypeface(Typeface.DEFAULT);
                        rbOpen.setChecked(true);
                        rbClose.setChecked(false);
                    }else{
                        rbOpen.setTextSize(14);
                        rbClose.setTextSize(18);
                        rbClose.setTypeface(Typeface.DEFAULT_BOLD);
                        rbOpen.setTypeface(Typeface.DEFAULT);
                        rbClose.setChecked(true);
                        rbOpen.setChecked(false);
                    }
                    dialog.dismiss();
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ID==1){
                        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY)
                                .child(menuCategoryName)
                                .child(productName)
                                .child("salesSituation")
                                .setValue("true");
                        Toast.makeText(ProductDetail.this,"Ürün satışa açık pozisyona getirildi",Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY)
                                .child(menuCategoryName)
                                .child(productName)
                                .child("salesSituation")
                                .setValue("false");
                        Toast.makeText(ProductDetail.this,"Ürün satışa kapalı pozisyona getirildi",Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
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
        rbClose=findViewById(R.id.rbClose);
        rbOpen=findViewById(R.id.rbOpen);
        rbCloseComment=findViewById(R.id.rbCloseComment);
        rbOpenComment=findViewById(R.id.rbOpenComment);
        btnBack=findViewById(R.id.btnBack);
        databaseReference= FirebaseDatabase.getInstance().getReference();
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
                            Glide.with(ProductDetail.this).load(addProductPost.getProductImageUrl()).into(productImage);
                        }
                        tvProductName.setText(addProductPost.getProductName());
                        tvPrice.setText(addProductPost.getProductPrice());
                        tvMenuTitle.setText(menuCategoryName);
                        tvProductDetail.setText(addProductPost.getProductExplanation());
                        tvTimeService.setText(addProductPost.getProductServiceTime());
                        if(addProductPost.getSalesSituation().equalsIgnoreCase("true")){
                            isOpen=true;
                            rbOpen.setTextSize(18);
                            rbClose.setTextSize(14);
                            rbOpen.setChecked(true);
                            rbOpen.setTypeface(Typeface.DEFAULT_BOLD);
                            rbClose.setTypeface(Typeface.DEFAULT);
                            rbClose.setChecked(false);
                        }else{
                            isOpen=false;
                            rbOpen.setTextSize(14);
                            rbClose.setTextSize(18);
                            rbClose.setChecked(true);
                            rbClose.setTypeface(Typeface.DEFAULT_BOLD);
                            rbOpen.setTypeface(Typeface.DEFAULT);
                            rbOpen.setChecked(false);
                        }

                        if(addProductPost.getCommentSituation().equalsIgnoreCase("true")){
                            isOpenComment=true;
                            rbOpenComment.setTextSize(18);
                            rbCloseComment.setTextSize(14);
                            rbOpenComment.setChecked(true);
                            rbOpenComment.setTypeface(Typeface.DEFAULT_BOLD);
                            rbCloseComment.setTypeface(Typeface.DEFAULT);
                            rbCloseComment.setChecked(false);
                        }else{
                            isOpenComment=false;
                            rbOpenComment.setTextSize(14);
                            rbCloseComment.setTextSize(18);
                            rbCloseComment.setChecked(true);
                            rbCloseComment.setTypeface(Typeface.DEFAULT_BOLD);
                            rbOpenComment.setTypeface(Typeface.DEFAULT);
                            rbOpenComment.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

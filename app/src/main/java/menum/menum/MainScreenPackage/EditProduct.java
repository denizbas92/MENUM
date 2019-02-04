package menum.menum.MainScreenPackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import menum.menum.Constant.MenumConstant;
import menum.menum.Model.AddProductPost;
import menum.menum.R;

public class EditProduct extends AppCompatActivity {

    private RelativeLayout relative;
    private EditText etProductName,etProductPrice,etProductExplanation,etProductServiceTime;
    private Button btnCancel,btnAdd;

    private static String phoneNumber;
    private static String menuCategoryName;
    private static String productName;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    public EditProduct(){

    }

    public EditProduct(String phoneNumber, String menuCategoryName, String productName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditProduct.this,Products.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        init();
        getProductFeatures();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(EditProduct.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
               checkIsEmpty();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProduct.this,Products.class));
                finish();
            }
        });

    }

    private void init() {
        relative=findViewById(R.id.relative);
        etProductName=findViewById(R.id.etProductName);
        etProductPrice=findViewById(R.id.etProductPrice);
        etProductExplanation=findViewById(R.id.etProductExplanation);
        etProductServiceTime=findViewById(R.id.etProductServiceTime);
        btnCancel=findViewById(R.id.btnCancel);
        btnAdd=findViewById(R.id.btnAdd);


        progressDialog=new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void getProductFeatures() {
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
                        etProductName.setText(addProductPost.getProductName());
                        etProductPrice.setText(addProductPost.getProductPrice());
                        etProductExplanation.setText(addProductPost.getProductExplanation());
                        etProductServiceTime.setText(addProductPost.getProductServiceTime());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void checkIsEmpty() {
        if(TextUtils.isEmpty(etProductName.getText().toString())){
            setNegativeToastMessage("Ürün Adı Giriniz");
            progressDialog.dismiss();
            return;
        }

        if(TextUtils.isEmpty(etProductPrice.getText().toString())){
            setNegativeToastMessage("Ürün Fiyatı Giriniz");
            progressDialog.dismiss();
            return;
        }

        final String newProductName=etProductName.getText().toString();
        if(newProductName.equals(productName)){
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_CATEGORY)
                    .child(menuCategoryName)
                    .child(newProductName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                            addProductPost.setProductName(etProductName.getText().toString());
                            addProductPost.setProductServiceTime(etProductServiceTime.getText().toString());
                            addProductPost.setProductExplanation(etProductExplanation.getText().toString());
                            addProductPost.setProductImageUrl(addProductPost.getProductImageUrl());
                            addProductPost.setProductPrice(etProductPrice.getText().toString());
                            addProductPost.setSalesSituation(addProductPost.getSalesSituation());
                            addProductPost.setCommentSituation(addProductPost.getCommentSituation());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_CATEGORY)
                                    .child(menuCategoryName)
                                    .child(newProductName)
                                    .setValue(addProductPost);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            progressDialog.dismiss();
            StyleableToast st=new StyleableToast(EditProduct.this,"Ürün Güncellendi.", Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#0000ff"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();

            startActivity(new Intent(EditProduct.this,Products.class));
            finish();
        }else{
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
                            addProductPost.setProductName(newProductName);
                            addProductPost.setProductPrice(etProductPrice.getText().toString());
                            addProductPost.setProductImageUrl(addProductPost.getProductImageUrl());
                            addProductPost.setProductExplanation(etProductExplanation.getText().toString());
                            addProductPost.setProductServiceTime(etProductServiceTime.getText().toString());
                            addProductPost.setSalesSituation(addProductPost.getSalesSituation());
                            addProductPost.setCommentSituation(addProductPost.getCommentSituation());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_CATEGORY)
                                    .child(menuCategoryName)
                                    .child(newProductName)
                                    .setValue(addProductPost);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            updateLikeDislikeCustomer(productName,newProductName);
            updateProductCommentCustomer(productName,newProductName);
            updateProductCommentStore(productName,newProductName);
            updateLikeDislikeStore(productName,newProductName);

            StyleableToast st=new StyleableToast(EditProduct.this,"Ürün Güncellendi." + productName, Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#0000ff"));
            st.setTextColor(Color.WHITE);
            st.setCornerRadius(2);
            st.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.MENU_CATEGORY)
                            .child(menuCategoryName)
                            .child(productName)
                            .removeValue();
                }
            },1500);

            progressDialog.dismiss();
            startActivity(new Intent(EditProduct.this,Products.class));
            finish();
        }
    }

    private void updateLikeDislikeStore(String productName, String newProductName) {
        final DatabaseReference dbFromLike=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_LIKE).child(menuCategoryName).child(productName);
        final DatabaseReference dbToLike=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_LIKE).child(menuCategoryName).child(newProductName);

        dbFromLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbToLike.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dbFromLike.removeValue();
                                }
                            },1000);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProductCommentStore(String productName, String newProductName) {
        final DatabaseReference dbFromProductComment=
                databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.PRODUCT_COMMENTS).child(menuCategoryName).child(productName);
        final DatabaseReference dbToProductComment=
                databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.PRODUCT_COMMENTS).child(menuCategoryName).child(newProductName);

        dbFromProductComment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbToProductComment.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dbFromProductComment.removeValue();
                                }
                            },1000);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProductCommentCustomer(final String productName, final String newProductName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.PRODUCT_COMMENTS)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.PRODUCT_COMMENTS)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName)
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        if(dataSnapshot.hasChild(productName)){
                                                                                                            final DatabaseReference dbFrom=databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(productName);
                                                                                                            final DatabaseReference dbTo=databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(newProductName);
                                                                                                            dbFrom.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                    dbTo.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                                                                                                                            if (databaseError != null) {
                                                                                                                                System.out.println("Copy failed");
                                                                                                                            } else {
                                                                                                                                Handler handler= new Handler();
                                                                                                                                handler.postDelayed(new Runnable() {
                                                                                                                                    @Override
                                                                                                                                    public void run() {
                                                                                                                                        dbFrom.removeValue();
                                                                                                                                    }
                                                                                                                                },1000);
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
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

    private void updateLikeDislikeCustomer(final String productName, final String newProductName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.LIKE_DISLIKE)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.LIKE_DISLIKE)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.LIKE_DISLIKE)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName)
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        if(dataSnapshot.hasChild(productName)){
                                                                                                            final DatabaseReference dbFrom=databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.LIKE_DISLIKE)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(productName);
                                                                                                            final DatabaseReference dbTo=databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.LIKE_DISLIKE)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(newProductName);
                                                                                                            dbFrom.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                    dbTo.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                                                                                                                            if (databaseError != null) {
                                                                                                                                System.out.println("Copy failed");
                                                                                                                            } else {
                                                                                                                                Handler handler= new Handler();
                                                                                                                                handler.postDelayed(new Runnable() {
                                                                                                                                    @Override
                                                                                                                                    public void run() {
                                                                                                                                        dbFrom.removeValue();
                                                                                                                                    }
                                                                                                                                },1000);
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
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

    private void setNegativeToastMessage(String message) {
        StyleableToast st=new StyleableToast(EditProduct.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff0000"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void setPositiveToastMessage(String message) {
        StyleableToast st=new StyleableToast(EditProduct.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }
}

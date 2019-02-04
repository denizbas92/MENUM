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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class EditProductImage extends AppCompatActivity {

    private Button btnAddImage,btnDelete,btnExit;
    private ImageView imProduct;
    private DatabaseReference databaseReference;

    private static String phoneNumber;
    private static String menuCategoryName ;
    private static String productName;

    private static final int GALLERY_INTENT=1;
    private static final int CAPTURE_CAMERA=2;

    private Uri uri ;
    private ProgressDialog progressDialog;
    private StorageReference firebaseStorage;

    public EditProductImage(){

    }

    public EditProductImage(String phoneNumber, String menuCategoryName, String productName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditProductImage.this,Products.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_image);
        init();
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProductImage.this,Products.class));
                finish();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadOption();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
            }
        });
    }

    private void init() {
        btnAddImage=findViewById(R.id.btnAddImage);
        btnDelete=findViewById(R.id.btnDelete);
        btnExit=findViewById(R.id.btnExit);
        imProduct=findViewById(R.id.imProduct);
        imProduct.setScaleType(ImageView.ScaleType.FIT_XY);

        progressDialog=new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage= FirebaseStorage.getInstance().getReference();
    }

    private void getImage() {
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
                            Glide.with(EditProductImage.this).load(addProductPost.getProductImageUrl()).fitCenter().into(imProduct);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void deleteImage() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MENU_CATEGORY)
                .child(menuCategoryName)
                .child(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                        if(addProductPost.getProductImageUrl().equalsIgnoreCase("")){
                            setNegativeToastMessage("Resim Bulunamadı");
                        }else{
                            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(EditProductImage.this);
                            View viewConfirm=LayoutInflater.from(EditProductImage.this).inflate(R.layout.layout_confirm_dialog,null);
                            TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
                            Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
                            Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
                            tvTitle.setText("Resim silinsin mi ?");
                            builder.setView(viewConfirm);
                            final android.app.AlertDialog dialog=builder.create();
                            dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                            dialog.show();

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final StorageReference storageReference =
                                            FirebaseStorage.getInstance().
                                                    getReferenceFromUrl(addProductPost.getProductImageUrl());
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            Log.e("firebasestorage", "onSuccess: deleted file");
                                            setPositiveToastMessage("Resim Silindi");
                                            databaseReference
                                                    .child(MenumConstant.STORE)
                                                    .child(phoneNumber)
                                                    .child(MenumConstant.MENU_CATEGORY)
                                                    .child(menuCategoryName)
                                                    .child(productName)
                                                    .child("productImageUrl")
                                                    .setValue("");
                                            databaseReference
                                                    .child(MenumConstant.STORE)
                                                    .child(phoneNumber)
                                                    .child(MenumConstant.MENU_LIKE)
                                                    .child(menuCategoryName)
                                                    .child(productName)
                                                    .child("productImageUrl")
                                                    .setValue("");
                                            startActivity(getIntent());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.e("firebasestorage", "onFailure: did not delete file");
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showUploadOption() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_gallery_or_capture,null);
        Button btnCapture=view.findViewById(R.id.btnCapture);
        Button btnGallery=view.findViewById(R.id.btnGallery);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck= ContextCompat.checkSelfPermission(EditProductImage.this, Manifest.permission.CAMERA);
                if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProductImage.this,new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    int permissionCheckRead= ContextCompat.checkSelfPermission(EditProductImage.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditProductImage.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
                    }else{
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAPTURE_CAMERA);
                        dialog.dismiss();
                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/+");
                startActivityForResult(intent,GALLERY_INTENT);
                dialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            uri = data.getData();
            Toast.makeText(EditProductImage.this, String.valueOf(uri), Toast.LENGTH_LONG).show();
            Log.e("merhaba", String.valueOf(uri));
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();
                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                imProduct.setBackground(dw);
                getOldImageUrl(1,progressDialog);
            } else if (requestCode == CAPTURE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();
                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                imProduct.setBackground(dw);
            }
        }catch (Exception e){
            Log.e("merhaba", e.getMessage().toString());
        }
    }

    private void getOldImageUrl(final int index, final ProgressDialog progressDialog){

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
                        if(addProductPost.getProductImageUrl().equalsIgnoreCase("")){
                            storeImage(progressDialog);
                        }else{
                            deleteOldImage(addProductPost.getProductImageUrl(),progressDialog);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void deleteOldImage(String imageUrl, final ProgressDialog progressDialog) {
        final StorageReference storageReference =
                FirebaseStorage.getInstance().
                        getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.e("firebasestorage", "onSuccess: deleted file");
                storeImage(progressDialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e("firebasestorage", "onFailure: did not delete file");
            }
        });
    }

    private void storeImage(final ProgressDialog progressDialog) {
        try {
            final StorageReference filePath = firebaseStorage
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.MENU_CATEGORY)
                    .child(menuCategoryName)
                    .child(getUri().getLastPathSegment());
            filePath.putFile(getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.MENU_CATEGORY)
                            .child(menuCategoryName)
                            .child(productName)
                            .child("productImageUrl")
                            .setValue(taskSnapshot.getDownloadUrl().toString());
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.MENU_LIKE)
                            .child(menuCategoryName)
                            .child(productName)
                            .child("productImageUrl")
                            .setValue(taskSnapshot.getDownloadUrl().toString());
                    progressDialog.dismiss();
                    startActivity(getIntent());
                    Toast.makeText(EditProductImage.this, "Resim Yüklendi", Toast.LENGTH_LONG).show();
                    Log.d("uzantı", String.valueOf(taskSnapshot.getDownloadUrl().toString()));
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplication(),"Eksik bilgi var",Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getUri() {
        return uri;
    }

    private void setNegativeToastMessage(String message) {
        StyleableToast st=new StyleableToast(EditProductImage.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff0000"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void setPositiveToastMessage(String message) {
        StyleableToast st=new StyleableToast(EditProductImage.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }
}

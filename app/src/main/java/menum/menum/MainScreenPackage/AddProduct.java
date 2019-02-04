package menum.menum.MainScreenPackage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

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

public class AddProduct extends AppCompatActivity {

    private RelativeLayout relative;
    private EditText etProductName,etProductPrice,etProductExplanation,etProductServiceTime;
    private Button btnUploadImage,btnCancel,btnAdd;
    private ImageView imProduct;

    private static String phoneNumber;
    private static String menuCategoryName;
    private static final int GALLERY_INTENT=1;
    private static final int CAPTURE_CAMERA=2;

    private Uri uri ;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference firebaseStorage;
    private static boolean isUploadImage=false;

    public AddProduct(){

    }

    public AddProduct(String phoneNumber, String menuCategoryName) {
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddProduct.this,Products.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        init();

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etProductName.getText().toString())){
                    Snackbar.make(view, "Ürün Adı Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(etProductPrice.getText().toString())){
                    Snackbar.make(view, "Ürün Fiyatı Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }
                showUploadOption();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(AddProduct.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                storeImage(1,progressDialog);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProduct.this,Products.class));
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
        btnUploadImage=findViewById(R.id.btnUploadImage);
        btnCancel=findViewById(R.id.btnCancel);
        btnAdd=findViewById(R.id.btnAdd);
        imProduct=findViewById(R.id.imProduct);
        imProduct.setScaleType(ImageView.ScaleType.FIT_XY);

        progressDialog=new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage= FirebaseStorage.getInstance().getReference();
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

                int permissionCheck= ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.CAMERA);
                if(permissionCheck!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddProduct.this,new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    int permissionCheckRead= ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddProduct.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
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
                int permissionCheckRead= ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddProduct.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9999);
                }else{
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/+");
                    startActivityForResult(intent,GALLERY_INTENT);
                    dialog.dismiss();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            uri = data.getData();
            Toast.makeText(AddProduct.this, String.valueOf(uri), Toast.LENGTH_LONG).show();
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
                isUploadImage=true;
                imProduct.setBackground(dw);
        //        getOldImageUrl(1, progressDialog);

            } else if (requestCode == CAPTURE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                isUploadImage=true;
                imProduct.setBackground(dw);
   //             getOldImageUrl(1, progressDialog);
            }
            }catch (Exception e){

        }
    }

    private void storeImage(final int index, final ProgressDialog progressDialog) {
        Log.e("uzantı", "girdi");
            if(isUploadImage){
                try{
                    final StorageReference filePath = firebaseStorage
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.MENU_CATEGORY)
                            .child(menuCategoryName)
                            .child(getUri().getLastPathSegment());
                    filePath.putFile(getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProduct.this, "Resim Yüklendi", Toast.LENGTH_LONG).show();
                            Log.d("uzantı", String.valueOf(taskSnapshot.getDownloadUrl().toString()));
                            checkIsEmpty(taskSnapshot.getDownloadUrl().toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("uzantı", e.getMessage().toString());
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(getApplication(),"Eksik bilgi var1"+ e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                //    checkIsEmpty("");
                }
            }else{
                checkIsEmpty("");
            }
    }

    private void checkIsEmpty(final String imageUrl) {
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

        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY).child(menuCategoryName).child(menuCategoryName).removeValue();
        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY).child(menuCategoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(etProductName.getText().toString())){
                            setNegativeToastMessage("Farklı ürün adı giriniz");
                            progressDialog.dismiss();
                        }else{
                            AddProductPost addProductPost=new AddProductPost();
                            addProductPost.setProductName(etProductName.getText().toString());
                            addProductPost.setProductPrice(etProductPrice.getText().toString());
                            addProductPost.setProductServiceTime(etProductServiceTime.getText().toString());
                            addProductPost.setProductExplanation(etProductExplanation.getText().toString());
                            addProductPost.setProductImageUrl(imageUrl);
                            addProductPost.setSalesSituation("true");
                            addProductPost.setCommentSituation("true");
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_CATEGORY)
                                    .child(menuCategoryName)
                                    .child(etProductName.getText().toString())
                                    .setValue(addProductPost);
                            progressDialog.dismiss();
                            isUploadImage=false;
                            setPositiveToastMessage("Ürün Eklendi");
                            startActivity(new Intent(AddProduct.this,Products.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setNegativeToastMessage(String message) {
        StyleableToast st=new StyleableToast(AddProduct.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff0000"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void setPositiveToastMessage(String message) {
        StyleableToast st=new StyleableToast(AddProduct.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    public Uri getUri() {
        return uri;
    }
}

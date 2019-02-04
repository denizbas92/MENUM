package menum.menum.PersonnelPackage;

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
import android.support.design.widget.FloatingActionButton;
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

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.AddProduct;
import menum.menum.MainScreenPackage.Products;
import menum.menum.Model.AddPersonnelPost;
import menum.menum.Model.AddProductPost;
import menum.menum.R;

public class EditPersonnel extends AppCompatActivity {

    private FloatingActionButton fabAddIcon;
    private CircleImageView imPersonnelIcon;
    private EditText etName,etSurName;
    private Button btnSave,btnBack;

    private static String phoneNumber;
    private static  String UID;

    private static final int GALLERY_INTENT=1;
    private static final int CAPTURE_CAMERA=2;

    private Uri uri ;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference firebaseStorage;
    private static boolean isUploadImage=false;

    public EditPersonnel(){

    }

    public EditPersonnel(String phoneNumber, String UID) {
        this.phoneNumber=phoneNumber;
        this.UID=UID;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditPersonnel.this,PersonnelMainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personnel);
        init();
        getPersonnelInfo();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditPersonnel.this,PersonnelMainScreen.class));
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsEmpty(view);
            }
        });

        fabAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etName.getText().toString())){
                    Snackbar.make(view, "İsim Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(etSurName.getText().toString())){
                    Snackbar.make(view, "Soyisim Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }
                showUploadOption();
            }
        });

        imPersonnelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PERSONNELS)
                        .child(UID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                if(addPersonnelPost.getImageUrl().equalsIgnoreCase("")==false){
                                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(EditPersonnel.this);
                                    View viewConfirm=LayoutInflater.from(EditPersonnel.this).inflate(R.layout.layout_confirm_dialog,null);
                                    TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
                                    Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
                                    Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
                                    tvTitle.setText("Kişi resmi silinecek. Bunu onaylıyor musunuz ?");
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
                                            databaseReference
                                                    .child(MenumConstant.STORE)
                                                    .child(phoneNumber)
                                                    .child(MenumConstant.PERSONNELS)
                                                    .child(UID)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                                            if(addPersonnelPost.getImageUrl().equalsIgnoreCase("")==false){
                                                                StorageReference storageReference =
                                                                        FirebaseStorage.getInstance().
                                                                                getReferenceFromUrl(addPersonnelPost.getImageUrl());
                                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // File deleted successfully
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception exception) {
                                                                        // Uh-oh, an error occurred!
                                                                        Log.e("firebasestorage", "onFailure: did not delete file");
                                                                    }
                                                                });
                                                            }
                                                            databaseReference
                                                                    .child(MenumConstant.STORE)
                                                                    .child(phoneNumber)
                                                                    .child(MenumConstant.PERSONNELS)
                                                                    .child(UID)
                                                                    .child("imageUrl")
                                                                    .setValue("");
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                            new android.os.Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    Toast.makeText(EditPersonnel.this,"Resim Silindi",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(EditPersonnel.this,PersonnelMainScreen.class));
                                                }
                                            },1000);
                                        }
                                    });
                                }else{
                                    Toast.makeText(EditPersonnel.this,"Resim Bulunamadı",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private void getPersonnelInfo() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNELS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                        etName.setText(addPersonnelPost.getName());
                        etSurName.setText(addPersonnelPost.getSurname());
                        if(addPersonnelPost.getImageUrl().equalsIgnoreCase("")==false){
                            Glide.with(EditPersonnel.this).load(addPersonnelPost.getImageUrl()).into(imPersonnelIcon);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void init() {
        btnSave=findViewById(R.id.btnSave);
        btnBack=findViewById(R.id.btnBack);
        etName=findViewById(R.id.etName);
        etSurName=findViewById(R.id.etSurName);
        imPersonnelIcon=findViewById(R.id.imPersonnelIcon);
        fabAddIcon=findViewById(R.id.fabAddIcon);

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

                int permissionCheck= ContextCompat.checkSelfPermission(EditPersonnel.this, Manifest.permission.CAMERA);
                if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditPersonnel.this,new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    int permissionCheckRead= ContextCompat.checkSelfPermission(EditPersonnel.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(EditPersonnel.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
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
                int permissionCheckRead= ContextCompat.checkSelfPermission(EditPersonnel.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditPersonnel.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
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
            Toast.makeText(EditPersonnel.this, String.valueOf(uri)+" Resim Bilgisi Alındı", Toast.LENGTH_LONG).show();
            Log.e("merhaba", String.valueOf(uri));
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                isUploadImage=true;
                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                imPersonnelIcon.setBackground(dw);

            } else if (requestCode == CAPTURE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                isUploadImage=true;
                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                imPersonnelIcon.setBackground(dw);
            }
        }catch (Exception e){

        }
    }


    private void getOldImageUrl(final int index,final ProgressDialog progressDialog){
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNELS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                        if(index==1){
                            try{
                                deleteOldImage(addPersonnelPost.getImageUrl(),index,progressDialog);
                            }catch (Exception e){
                                storeImage(1,progressDialog);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void deleteOldImage(String imageUrl,final int index,final ProgressDialog progressDialog) {
        StorageReference storageReference =
                FirebaseStorage.getInstance().
                        getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.e("firebasestorage", "onSuccess: deleted file");
                storeImage(1,progressDialog);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e("firebasestorage", "onFailure: did not delete file");
            }
        });
    }

    private void storeImage(final int index, final ProgressDialog progressDialog) {
                try {
                    final StorageReference filePath = firebaseStorage
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.PERSONNELS)
                            .child(getUri().getLastPathSegment());
                    filePath.putFile(getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditPersonnel.this, "Personel Bilgisi Güncellendi", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            Log.d("uzantı", String.valueOf(taskSnapshot.getDownloadUrl().toString()));
                            AddPersonnelPost addPersonnelPost=new AddPersonnelPost();
                            addPersonnelPost.setName(etName.getText().toString());
                            addPersonnelPost.setSurname(etSurName.getText().toString());
                            addPersonnelPost.setVoteRate("0");
                            addPersonnelPost.setUID(UID);
                            addPersonnelPost.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber).child(MenumConstant.PERSONNELS)
                                    .child(UID)
                                    .setValue(addPersonnelPost);
                            isUploadImage=false;
                            progressDialog.dismiss();
                            startActivity(new Intent(EditPersonnel.this,PersonnelMainScreen.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("hata",e.getMessage().toString());
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(getApplication(),"Eksik bilgi var",Toast.LENGTH_SHORT).show();
                }
    }

    private void checkIsEmpty(View view) {
        if(TextUtils.isEmpty(etName.getText().toString())){
            Snackbar.make(view, "İsim Giriniz", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(etSurName.getText().toString())){
            Snackbar.make(view, "Soyisim Giriniz", Snackbar.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(EditPersonnel.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.show();

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNELS)
                .child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                        if(isUploadImage){
                            if(addPersonnelPost.getImageUrl().equalsIgnoreCase("")){
                                isUploadImage=false;
                                storeImage(1,progressDialog);
                            }else{
                                isUploadImage=false;
                                getOldImageUrl(1,progressDialog);
                            }
                        }else{
                                addPersonnelPost.setName(etName.getText().toString());
                                addPersonnelPost.setSurname(etSurName.getText().toString());
                                addPersonnelPost.setVoteRate("0");
                                addPersonnelPost.setUID(UID);
                                addPersonnelPost.setImageUrl(addPersonnelPost.getImageUrl());
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber).child(MenumConstant.PERSONNELS)
                                        .child(UID)
                                        .setValue(addPersonnelPost);
                                isUploadImage=false;
                                progressDialog.dismiss();
                                startActivity(new Intent(EditPersonnel.this,PersonnelMainScreen.class));
                                finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public Uri getUri() {
        return uri;
    }
}

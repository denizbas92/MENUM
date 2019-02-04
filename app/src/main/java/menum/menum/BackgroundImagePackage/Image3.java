package menum.menum.BackgroundImagePackage;


import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import menum.menum.MainScreenPackage.Settings;
import menum.menum.Model.BackGroundImagePost;
import menum.menum.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Image3 extends Fragment {

    private TextView tvChangeAdd,tvDelete;
    private ImageView image3;
    private ProgressDialog progressDialog;

    private static String phoneNumber;
    private static final int GALLERY_INTENT=1;
    private static final int CAPTURE_CAMERA=2;
    private DatabaseReference databaseReference;
    private StorageReference firebaseStorage;
    private Uri uri ;

    public Image3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_image1, container, false);
        tvChangeAdd=view.findViewById(R.id.tvChangeAdd);
        tvDelete=view.findViewById(R.id.tvDelete);
        image3=view.findViewById(R.id.image1);
        image3.setScaleType(ImageView.ScaleType.FIT_XY);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage= FirebaseStorage.getInstance().getReference();
        Settings settings=new Settings();
        phoneNumber=settings.getPhoneNumber();
        setBackgroundImage(container);
        tvChangeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadOption(container);
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(container);
            }
        });

        return view;
    }

    private void setBackgroundImage(final ViewGroup container) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.BACKGROUND_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        BackGroundImagePost backGroundImagePost=dataSnapshot.getValue(BackGroundImagePost.class);
                        try{
                            if(backGroundImagePost.getImage3().equalsIgnoreCase("")==false){
                                Glide.with(container.getContext()).load(backGroundImagePost.getImage3()).into(image3);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void delete(final ViewGroup container) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getContext());
        View viewConfirm=LayoutInflater.from(getContext()).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        tvTitle.setText("Resim silinsin mi ?");
        tvTitle.setTextSize(18);
        Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
        Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
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
                        .child(MenumConstant.BACKGROUND_IMAGES)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                BackGroundImagePost backGroundImagePost=dataSnapshot.getValue(BackGroundImagePost.class);
                                try{
                                    if(backGroundImagePost.getImage3().equalsIgnoreCase("")){
                                        StyleableToast st=new StyleableToast(container.getContext(),"Resim Bulunamadı", Toast.LENGTH_LONG);
                                        st.setBackgroundColor(Color.parseColor("#ff0000"));
                                        st.setTextColor(Color.WHITE);
                                        st.setCornerRadius(2);
                                        st.show();
                                    }else{
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance().
                                                        getReferenceFromUrl(backGroundImagePost.getImage3());
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                Log.e("firebasestorage", "onSuccess: deleted file");
                                                databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.BACKGROUND_IMAGES).child("image3").setValue("");
                                                StyleableToast st=new StyleableToast(container.getContext(),"Resim Silindi", Toast.LENGTH_LONG);
                                                st.setBackgroundColor(Color.parseColor("#0000ff"));
                                                st.setTextColor(Color.WHITE);
                                                st.setCornerRadius(2);
                                                st.show();
                                                container.getContext().startActivity(new Intent(container.getContext(), BackgroundImage.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {

                                                Log.e("firebasestorage", "onFailure: did not delete file");
                                            }
                                        });
                                    }
                                }catch (Exception e){
                                    StyleableToast st=new StyleableToast(container.getContext(),"Resim Bulunamadı", Toast.LENGTH_LONG);
                                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                                    st.setTextColor(Color.WHITE);
                                    st.setCornerRadius(2);
                                    st.show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private void showUploadOption(final ViewGroup container) {
        AlertDialog.Builder builder=new AlertDialog.Builder(container.getContext());
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.layout_gallery_or_capture,null);
        Button btnCapture=view.findViewById(R.id.btnCapture);
        Button btnGallery=view.findViewById(R.id.btnGallery);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck= ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.CAMERA);
                if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) container.getContext(),new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    int permissionCheckRead= ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) container.getContext(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            uri = data.getData();
            //         Log.e("merhaba", String.valueOf(uri));
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                Log.e("merhaba", String.valueOf(uri));
                image3.setBackground(dw);
                getOldImageUrl(1, progressDialog);
            } else if (requestCode == CAPTURE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                image3.setBackground(dw);
                getOldImageUrl(1, progressDialog);
            }
        }catch (Exception e){
//            Log.e("merhaba", String.valueOf(uri) + e.getMessage().toString());
        }
    }

    private void getOldImageUrl(final int index,final ProgressDialog progressDialog){
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.BACKGROUND_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        BackGroundImagePost backGroundImagePost=dataSnapshot.getValue(BackGroundImagePost.class);
                        try{
                            deleteOldImage(backGroundImagePost.getImage3(),index,progressDialog);
                        }catch (Exception e){
                            storeImage(1,progressDialog);
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
                if(index==1){
                    databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.BACKGROUND_IMAGES).child("image3").setValue("");
                    storeImage(1,progressDialog);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.BACKGROUND_IMAGES).child("image3").setValue("");
                storeImage(1,progressDialog);
                Log.e("firebasestorage", "onFailure: did not delete file");
            }
        });
    }

    private void storeImage(final int index, final ProgressDialog progressDialog) {
        try {
            final StorageReference filePath = firebaseStorage
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.BACKGROUND_IMAGES)
                    .child(getUri().getLastPathSegment());
            filePath.putFile(getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Resim Yüklendi", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.d("uzantı", String.valueOf(taskSnapshot.getDownloadUrl().toString()));
                    BackGroundImagePost backGroundImagePost = new BackGroundImagePost();
                    if (index == 1) {
                        backGroundImagePost.setImage3(taskSnapshot.getDownloadUrl().toString());
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.BACKGROUND_IMAGES)
                                .child("image3").setValue(backGroundImagePost.getImage3());
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(),"Eksik bilgi var",Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getUri() {
        return uri;
    }
}

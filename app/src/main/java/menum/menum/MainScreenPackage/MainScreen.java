package menum.menum.MainScreenPackage;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
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
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.AdviceNotification.AdviceService;
import menum.menum.CommentNotification.CommentService;
import menum.menum.Constant.MenumConstant;
import menum.menum.InternetService.CheckInternet;
import menum.menum.LikeNotification.LikeService;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.MenuCommentPackage.MenuCommentCategory;
import menum.menum.Model.BackGroundImagePost;
import menum.menum.Model.NewNotificationPost;
import menum.menum.Model.StorePropertyPost;
import menum.menum.PersonnelPackage.PersonnelMainScreen;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.NotificationAdapter;
import menum.menum.VoteNotification.VoteService;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mainLayout,cardTopLayout;
    private Button btnMenu,btnPersonnel,btnMenuComment,btnAdviceComplaint,btnQR,btnCampaign,btnSettings;
    private FloatingActionButton fabAddIcon,fabNot,fabLogOut;
    private CircleImageView imStoreIcon;
    private TextView tvStoreName;

    private static String phoneNumber ;
    private static String userName ;
    private static String storeName;

    private NotificationAdapter notificationAdapter;
    private NewNotificationPost newNotificationPost;
    private List<NewNotificationPost> newNotificationPostList;
    private NotificationBadge notificationBadge;

    private DatabaseReference databaseReference;
    private static final int GALLERY_INTENT=1;
    private static final int CAPTURE_CAMERA=2;
    private StorageReference firebaseStorage;
    private ProgressDialog progressDialog;
    private Uri uri ;
    private static int seenCounter=0;
    private static int counterChild=0;

    public MainScreen (){

    }

    public MainScreen(String phoneNumber, String userName, String storeName) {
        this.phoneNumber=phoneNumber;
        this.userName=userName;
        this.storeName=storeName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainScreen.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MainScreen.this, AdviceService.class));
        startService(new Intent(MainScreen.this, CommentService.class));
        startService(new Intent(MainScreen.this, LikeService.class));
        startService(new Intent(MainScreen.this, VoteService.class));
        startService(new Intent(MainScreen.this, CheckInternet.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainScreen.this, CheckInternet.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        init();
        setBadgeNumber();
        setStoreIcon();
        setStoreCardBackgroundImage();
    }

    private void init() {
        mainLayout=findViewById(R.id.mainLayout);
        cardTopLayout=findViewById(R.id.cardTopLayout);
        btnMenu=findViewById(R.id.btnMenu);
        btnPersonnel=findViewById(R.id.btnPersonnel);
        btnMenuComment=findViewById(R.id.btnMenuComment);
        btnAdviceComplaint=findViewById(R.id.btnAdviceComplaint);
        btnQR=findViewById(R.id.btnQR);
        btnCampaign=findViewById(R.id.btnCampaign);
        btnSettings=findViewById(R.id.btnSettings);
        fabAddIcon=findViewById(R.id.fabAddIcon);
        fabNot=findViewById(R.id.fabNot);
        fabLogOut=findViewById(R.id.fabLogOut);
        notificationBadge=findViewById(R.id.badge);

        btnMenu.setOnClickListener(this);
        btnPersonnel.setOnClickListener(this);
        btnMenuComment.setOnClickListener(this);
        btnAdviceComplaint.setOnClickListener(this);
        btnQR.setOnClickListener(this);
        btnCampaign.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        fabAddIcon.setOnClickListener(this);
        fabNot.setOnClickListener(this);
        fabLogOut.setOnClickListener(this);

        imStoreIcon=findViewById(R.id.imStoreIcon);
        tvStoreName=findViewById(R.id.tvStoreName);
        newNotificationPostList=new ArrayList<>();

        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage= FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMenu:
                    new MenuCategory(phoneNumber);
                    startActivity(new Intent(MainScreen.this,MenuCategory.class));
                    finish();
                break;
            case R.id.btnPersonnel:
                    new PersonnelMainScreen(phoneNumber);
                    startActivity(new Intent(MainScreen.this,PersonnelMainScreen.class));
                    finish();
                break;
            case R.id.btnMenuComment:
                    new MenuCommentCategory(phoneNumber);
                    startActivity(new Intent(MainScreen.this,MenuCommentCategory.class));
                    finish();
                break;
            case R.id.btnAdviceComplaint:
                    new ShowAdviceComplaint(phoneNumber);
                    startActivity(new Intent(MainScreen.this,ShowAdviceComplaint.class));
                    finish();
                break;
            case R.id.btnQR:
                    new QRCodeGenerator(phoneNumber);
                    startActivity(new Intent(MainScreen.this,QRCodeGenerator.class));
                    finish();
                break;
            case R.id.btnCampaign:
                    new Campaigns(phoneNumber);
                    startActivity(new Intent(MainScreen.this,Campaigns.class));
                    finish();
                break;
            case R.id.btnSettings:
                    new Settings(phoneNumber);
                    startActivity(new Intent(MainScreen.this,Settings.class));
                    finish();
                break;
            case R.id.fabAddIcon:
                    showUploadOption();
                break;
            case R.id.fabNot:
                    notification();
                break;
            case R.id.fabLogOut:
                    startActivity(new Intent(MainScreen.this, LoginActivity.class));
                    finish();
                break;
        }
    }

    private void setStoreIcon() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.STORE_PROPERTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                        try{
                            if(storePropertyPost.getIconStore().equalsIgnoreCase("")==false){
                                Glide.with(MainScreen.this).load(storePropertyPost.getIconStore()).into(imStoreIcon);
                            }
                            tvStoreName.setText(storePropertyPost.getStoreName());
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setBadgeNumber() {
        seenCounter=0;
        counterChild=0;
        newNotificationPostList.clear();
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.NEW_NOTIFICATION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot snapData) {
                        for(final DataSnapshot snapDate:snapData.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION)
                                    .child(snapDate.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapHour:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.NOTIFICATIONS)
                                                        .child(MenumConstant.NEW_NOTIFICATION)
                                                        .child(snapDate.getKey().toString())
                                                        .child(snapHour.getKey().toString())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                NewNotificationPost newNotificationPost=dataSnapshot.getValue(NewNotificationPost.class);
                                                                if(newNotificationPost.getSeen().equalsIgnoreCase("false")){
                                                                    seenCounter++;
                                                                    Log.d("seenCounter",Integer.toString(seenCounter));
                                                                }
                                                                notificationBadge.setNumber(seenCounter);
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

    private void setStoreCardBackgroundImage() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.BACKGROUND_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        BackGroundImagePost backGroundImagePost=dataSnapshot.getValue(BackGroundImagePost.class);
                        String image1=backGroundImagePost.getImage1();
                        String image2=backGroundImagePost.getImage2();
                        String image3=backGroundImagePost.getImage3();
                        if(image1.equalsIgnoreCase("")==false){
                            if(image2.equalsIgnoreCase("")){
                                ViewTarget viewTarget=new ViewTarget(mainLayout);
                                Glide.with(getApplicationContext()).load(image1).fitCenter().centerCrop().into(viewTarget);
                            }else{
                                setFirstImage(image1,image2,image3);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setFirstImage(final String image1, final String image2, final String image3) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTarget viewTarget=new ViewTarget(mainLayout);
                Glide.with(getApplicationContext()).load(image1).fitCenter().centerCrop().into(viewTarget);
                setSecondImage(image1,image2,image3);
            }
        },5000);
    }

    private void setSecondImage(final String image1, final String image2, final String image3) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTarget viewTarget=new ViewTarget(mainLayout);
                Glide.with(getApplicationContext()).load(image2).fitCenter().centerCrop().into(viewTarget);
                if(image3.equalsIgnoreCase("")){
                    setFirstImage(image1,image2,image3);
                }else{
                    setThirdImage(image1,image2,image3);
                }
            }
        },5000);
    }

    private void setThirdImage(final String image1, final String image2, final String image3) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTarget viewTarget=new ViewTarget(mainLayout);
                Glide.with(getApplicationContext()).load(image3).fitCenter().centerCrop().into(viewTarget);
                setFirstImage(image1,image2,image3);
            }
        },5000);
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

                int permissionCheck= ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.CAMERA);
                if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) MainScreen.this,new String[]{Manifest.permission.CAMERA},CAPTURE_CAMERA);
                }else{
                    int permissionCheckRead= ContextCompat.checkSelfPermission(MainScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheckRead!=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) MainScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},999);
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
                progressDialog = new ProgressDialog(MainScreen.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                Log.e("merhaba", String.valueOf(uri));
                imStoreIcon.setBackground(dw);
                getOldImageUrl(1, progressDialog);
            } else if (requestCode == CAPTURE_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {
                progressDialog = new ProgressDialog(MainScreen.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Yükleniyor...");
                progressDialog.show();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String tempFile = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImage = BitmapFactory.decodeFile(tempFile);
                Drawable dw = new BitmapDrawable(selectedImage);
                imStoreIcon.setBackground(dw);
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
                .child(MenumConstant.STORE_PROPERTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                        try{
                            deleteOldImage(storePropertyPost.getIconStore(),index,progressDialog);
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
                    databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.STORE_PROPERTY).child("iconStore").setValue("");
                    storeImage(1,progressDialog);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.STORE_PROPERTY).child("iconStore").setValue("");
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
                    .child(MenumConstant.STORE_PROPERTY)
                    .child(getUri().getLastPathSegment());
            filePath.putFile(getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(MainScreen.this, "Resim Yüklendi", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Log.d("uzantı", String.valueOf(taskSnapshot.getDownloadUrl().toString()));
                    StorePropertyPost storePropertyPost = new StorePropertyPost();
                    if (index == 1) {
                        storePropertyPost.setIconStore(taskSnapshot.getDownloadUrl().toString());
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.STORE_PROPERTY)
                                .child("iconStore").setValue(storePropertyPost.getIconStore());
                        startActivity(getIntent());
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(MainScreen.this,"Eksik bilgi var",Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getUri() {
        return uri;
    }

    private void notification() {
        newNotificationPostList.clear();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.activity_notifications,null);
        TextView tvDeleteAll=view.findViewById(R.id.tvDeleteAll);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        final RecyclerView recNewNotification=view.findViewById(R.id.recNewNotification);
        recNewNotification.setLayoutManager(linearLayoutManager);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAll();
            }
        });

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.NOTIFICATIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.NEW_NOTIFICATION)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(final DataSnapshot snapDate:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.NOTIFICATIONS)
                                                        .child(MenumConstant.NEW_NOTIFICATION)
                                                        .child(snapDate.getKey().toString())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot snapHour:dataSnapshot.getChildren()){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.NOTIFICATIONS)
                                                                            .child(MenumConstant.NEW_NOTIFICATION)
                                                                            .child(snapDate.getKey().toString())
                                                                            .child(snapHour.getKey().toString())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    NewNotificationPost newNotificationPost=dataSnapshot.getValue(NewNotificationPost.class);
                                                                                    newNotificationPostList.add(newNotificationPost);
                                                                                    notificationAdapter=new NotificationAdapter(MainScreen.this,phoneNumber,newNotificationPostList);
                                                                                    recNewNotification.setAdapter(notificationAdapter);
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
                        }else{
                            StyleableToast st=new StyleableToast(MainScreen.this,"Yeni Bildirim Bulunamadı", Toast.LENGTH_LONG);
                            st.setBackgroundColor(Color.parseColor("#0000ff"));
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

    private void deleteAll() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainScreen.this);
        View view=LayoutInflater.from(MainScreen.this).inflate(R.layout.layout_confirm_dialog,null);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        Button btnConfirm=view.findViewById(R.id.btnConfirm);
        TextView tvTitle=view.findViewById(R.id.tvTitle);
        tvTitle.setText("Bildirimlerin tamamı silinecek. Bunu onaylıyor musunuz?");
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

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION)
                        .removeValue();
                StyleableToast st=new StyleableToast(MainScreen.this,"Bildirimlerin Tamamı Silindi", Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#0000ff"));
                st.setTextColor(Color.WHITE);
                st.setCornerRadius(2);
                st.show();
                dialog.dismiss();
            }
        });
    }

    public class ViewTarget extends ViewTarget2<GlideDrawable> {

        public ViewTarget(ViewGroup view) {
            super(view);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setBackground(resource);
        }
    }
    public abstract class ViewTarget2<Z> extends com.bumptech.glide.request.target.ViewTarget<ViewGroup,Z> implements GlideAnimation.ViewAdapter {


        public ViewTarget2(ViewGroup view) {
            super(view);
        }

        @Override
        public Drawable getCurrentDrawable() {
            return view.getBackground();
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param drawable {@inheritDoc}
         */
        @Override
        public void setDrawable(Drawable drawable) {
            view.setBackground(drawable);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param placeholder {@inheritDoc}
         */
        @Override
        public void onLoadStarted(Drawable placeholder) {
            view.setBackground(placeholder);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param errorDrawable {@inheritDoc}
         */
        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            view.setBackground(errorDrawable);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param placeholder {@inheritDoc}
         */
        @Override
        public void onLoadCleared(Drawable placeholder) {
            view.setBackground(placeholder);
        }

        @Override
        public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {

            this.setResource(resource);
        }

        protected abstract void setResource(Z resource);

    }
}

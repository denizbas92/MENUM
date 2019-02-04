package menum.menum.CustomerPackage;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import menum.menum.CampaignPackage.CampaignService;
import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.Model.BackGroundImagePost;
import menum.menum.Model.CommentPost;
import menum.menum.Model.GoToCampaignPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.Model.StorePropertyPost;
import menum.menum.R;
import menum.menum.RoomDatabase.AbstractPackage.CustomerAppDatabase;
import menum.menum.RoomDatabase.Model.Customer;
import menum.menum.RoomDatabase.Model.User;

public class CustomerMainScreen extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relative;
    private Button btnPersonnel,btnRegister,btnMenu,btnAdviceComplaint,btnCampaign;
    private List<Customer> customerList;

    private static String phoneNumber;
    private DatabaseReference databaseReference;

    public CustomerMainScreen(){

    }

    public CustomerMainScreen(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_screen);
        init();
        if(getImeiNumber().equalsIgnoreCase("")==false){
            saveCustomer();
        }
        setStoreCardBackgroundImage();
    }

    private void init() {
        relative=findViewById(R.id.relative);
        btnPersonnel=findViewById(R.id.btnPersonnel);
        btnRegister=findViewById(R.id.btnRegister);
        btnMenu=findViewById(R.id.btnMenu);
        btnAdviceComplaint=findViewById(R.id.btnAdviceComplaint);
        btnCampaign=findViewById(R.id.btnCampaign);

        btnPersonnel.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnAdviceComplaint.setOnClickListener(this);
        btnCampaign.setOnClickListener(this);

        databaseReference= FirebaseDatabase.getInstance().getReference();
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
                                ViewTarget viewTarget=new ViewTarget(relative);
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
                ViewTarget viewTarget=new ViewTarget(relative);
                Glide.with(getApplicationContext()).load(image1).fitCenter().centerCrop().into(viewTarget);
                setSecondImage(image1,image2,image3);
            }
        },5000);
    }

    private void setSecondImage(final String image1, final String image2, final String image3) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTarget viewTarget=new ViewTarget(relative);
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
                ViewTarget viewTarget=new ViewTarget(relative);
                Glide.with(getApplicationContext()).load(image3).fitCenter().centerCrop().into(viewTarget);
                setFirstImage(image1,image2,image3);
            }
        },5000);
    }

    private void saveCustomer() {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                .child("isClose")
                .setValue("false");
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MYCUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(getImeiNumber())){
                            CommentPost commentPost=new CommentPost();
                            commentPost.setImeiNumber(getImeiNumber());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MYCUSTOMERS)
                                    .child(getImeiNumber())
                                    .setValue(commentPost);
                            NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                            notificationCounterPost.setOldCounter("0");
                            notificationCounterPost.setNewCounter("1");
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.NEW_CAMPAIGN)
                                    .setValue(notificationCounterPost);

                            GoToCampaignPost goToCampaignPost=new GoToCampaignPost("","","","");
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.GOTO_CAMPAIGN)
                                    .setValue(goToCampaignPost);
                        }else{
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                                    .child("isClose").setValue("false");

                            CommentPost commentPost=new CommentPost();
                            commentPost.setImeiNumber(getImeiNumber());
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MYCUSTOMERS)
                                    .child(getImeiNumber())
                                    .setValue(commentPost);
                            NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                            notificationCounterPost.setOldCounter("0");
                            notificationCounterPost.setNewCounter("1");
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.NEW_CAMPAIGN)
                                    .setValue(notificationCounterPost);

                            GoToCampaignPost goToCampaignPost=new GoToCampaignPost("","","","");
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.GOTO_CAMPAIGN)
                                    .setValue(goToCampaignPost);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPersonnel:
                getPersonnel();
                break;
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnMenu:
                getMenu();
                break;
            case R.id.btnAdviceComplaint:
                adviceComplaint();
                break;
            case R.id.btnCampaign:
                getCampaign();
                break;
        }
    }

    private void getCampaign() {
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(CustomerMainScreen.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        if(customerList.size()==0){
            Toast.makeText(CustomerMainScreen.this,"Lütfen Kayıt Olunuz",Toast.LENGTH_SHORT).show();
        }else{
            databaseReference
                    .child(MenumConstant.STORE)
                    .child(phoneNumber)
                    .child(MenumConstant.STORE_PROPERTY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                            String storeName=storePropertyPost.getStoreName();
                            new CampaignForCustomer(phoneNumber,storeName);
                            startActivity(new Intent(CustomerMainScreen.this,CampaignForCustomer.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void adviceComplaint() {
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(CustomerMainScreen.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        if(customerList.size()==0){
            Toast.makeText(CustomerMainScreen.this,"Lütfen Kayıt Olunuz",Toast.LENGTH_SHORT).show();
        }else{
            new AdviceComplaint(phoneNumber);
            startActivity(new Intent(CustomerMainScreen.this,AdviceComplaint.class));
            finish();
        }
    }

    private void getPersonnel() {
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(CustomerMainScreen.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        if(customerList.size()==0){
            Toast.makeText(CustomerMainScreen.this,"Lütfen Kayıt Olunuz",Toast.LENGTH_SHORT).show();
        }else{
            new ShowPersonnel(phoneNumber);
            startActivity(new Intent(CustomerMainScreen.this,ShowPersonnel.class));
            finish();
        }
    }

    private void getMenu() {
        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(CustomerMainScreen.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();
        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        if(customerList.size()==0){
            Toast.makeText(CustomerMainScreen.this,"Lütfen Kayıt Olunuz",Toast.LENGTH_SHORT).show();
        }else{
            new MenuCategoryCustomer(phoneNumber);
            startActivity(new Intent(CustomerMainScreen.this,MenuCategoryCustomer.class));
            finish();
        }
    }

    private void register() {

        final CustomerAppDatabase customerAppDatabase=
                Room.databaseBuilder(CustomerMainScreen.this,CustomerAppDatabase.class,"CustomerInfo")
                        .allowMainThreadQueries()
                        .build();

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_customer_register,null);
        Button btnExit=view.findViewById(R.id.btnExit);
        Button btnSave=view.findViewById(R.id.btnSave);
        final EditText etName=view.findViewById(R.id.etName);
        final EditText etSurname=view.findViewById(R.id.etSurName);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        customerList=customerAppDatabase.customerUserDao().getAllInfo();
        if(customerList.size()!=0){
            etName.setText(customerList.get(0).getName());
            etSurname.setText(customerList.get(0).getSurname());
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etName.getText().toString())){
                    Snackbar.make(view,"İsim Giriniz",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(etSurname.getText().toString())){
                    Snackbar.make(view,"Soyisim Giriniz",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                customerList=customerAppDatabase.customerUserDao().getAllInfo();
                if(customerList.size()==0){
                    Customer customer=new Customer(etName.getText().toString(),etSurname.getText().toString());
                    customerAppDatabase.customerUserDao().inserUser(customer);
                }else{
                    customerList=customerAppDatabase.customerUserDao().getAllInfo();
                    if(customerList.get(0).getName().equalsIgnoreCase(etName.getText().toString())==false){
                        customerList.get(0).setName(etName.getText().toString());
                        customerAppDatabase.customerUserDao().updateUser(customerList.get(0));
                    }

                    if(customerList.get(0).getSurname().equalsIgnoreCase(etSurname.getText().toString())==false){
                        customerList.get(0).setSurname(etSurname.getText().toString());
                        customerAppDatabase.customerUserDao().updateUser(customerList.get(0));
                    }
                }

                Toast.makeText(CustomerMainScreen.this,"Kayıt Tamamlandı",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public String getImeiNumber(){
        int permissionCheck= ContextCompat.checkSelfPermission(CustomerMainScreen.this, Manifest.permission.READ_PHONE_STATE);
        TelephonyManager telephonyManager = (TelephonyManager) CustomerMainScreen.this.getSystemService(TELEPHONY_SERVICE);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) CustomerMainScreen.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            return "";
        }else{
            String deviceIMEI = telephonyManager.getDeviceId();
            startService(new Intent(CustomerMainScreen.this, CampaignService.class));
            return deviceIMEI;
        }
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

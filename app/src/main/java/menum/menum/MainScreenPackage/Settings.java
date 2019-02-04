package menum.menum.MainScreenPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.concurrent.TimeUnit;

import menum.menum.BackgroundImagePackage.BackgroundImage;
import menum.menum.Constant.MenumConstant;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.Model.NewNotificationSettings;
import menum.menum.Model.StorePropertyPost;
import menum.menum.R;
import menum.menum.RegisterScreenPackage.RegisterBoss;

public class Settings extends AppCompatActivity {
    private static Context context;
    private CardView cardPhoneNumber,cardAccount,cardPassword,cardNotification,cardBackgroundImage,cardUserName,cardResetPassword;
    private Button btnBack;

    private static String phoneNumber;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> adapter ;

    private EditText etPhone;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId;
    private static boolean isSucces=false;

    AlertDialog alertDialog_VertificationCode;
    AlertDialog dialogPhoneSettings;

    public Settings() {
    }

    public Settings(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Settings.this,MainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        onClick();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this,MainScreen.class));
                finish();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Settings.this,"Geçersiz Telefon Numarası",Toast.LENGTH_SHORT).show();
                    alertDialog_VertificationCode.dismiss();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void init() {
        cardPhoneNumber=findViewById(R.id.cardPhoneNumber);
        cardAccount=findViewById(R.id.cardAccount);
        cardPassword=findViewById(R.id.cardPassword);
        cardNotification=findViewById(R.id.cardNotification);
        cardBackgroundImage=findViewById(R.id.cardBackgroundImage);
        cardResetPassword=findViewById(R.id.cardResetPassword);
        cardUserName=findViewById(R.id.cardUserName);
        btnBack=findViewById(R.id.btnBack);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    private void onClick() {
        cardPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(1);
            }
        });
        cardUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(2);
            }
        });
        cardAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(3);
            }
        });
        cardPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(4);
            }
        });
        cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(5);
            }
        });
        cardBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(6);
            }
        });
        cardResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordControl(7);
            }
        });
    }

    private void passwordControl(final int id) {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_password_control,null);
        final EditText etPassword=view.findViewById(R.id.etPassword);
        etPassword.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        Button btnExit=view.findViewById(R.id.btnExit);
        Button btnControl=view.findViewById(R.id.btnControl);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etPassword.getText().toString())){
                    StyleableToast st=new StyleableToast(Settings.this,"Şifrenizi Giriniz", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                    return;
                }

                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.STORE_PROPERTY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                if(storePropertyPost.getPassword().equalsIgnoreCase(etPassword.getText().toString())){
                                    switch (id){
                                        case 1:
                                            dialog.dismiss();
                                            phoneSettings();
                                            break;
                                        case 2:
                                            dialog.dismiss();
                                            userNameSettings();
                                            break;
                                        case 3:
                                            dialog.dismiss();
                                            accountSettings();
                                            break;
                                        case 4:
                                            dialog.dismiss();
                                            passwordSettings();
                                            break;
                                        case 5:
                                            dialog.dismiss();
                                            notificationSettings();
                                            break;
                                        case 6:
                                            dialog.dismiss();
                                            backgroundImageSettings();
                                            break;
                                        case 7:
                                            dialog.dismiss();
                                            changeResetQuestion();
                                            break;
                                    }
                                }else{
                                    StyleableToast st=new StyleableToast(Settings.this,"Yanlış Parola", Toast.LENGTH_LONG);
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

    private void changeResetQuestion() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_reset_question_settings,null);
        final RadioButton rbFood=view.findViewById(R.id.rbFood);
        final RadioButton rbCity=view.findViewById(R.id.rbCity);
        final RadioButton rbBook=view.findViewById(R.id.rbBook);
        final TextView tvQuestion=view.findViewById(R.id.tvQuestion);
        final TextView tvAnswer=view.findViewById(R.id.tvAnswer);
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnExit=view.findViewById(R.id.btnExit);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rbFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbFood.getText().toString(),tvQuestion,tvAnswer);
            }
        });

        rbBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbBook.getText().toString(),tvQuestion,tvAnswer);
            }
        });

        rbCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbCity.getText().toString(),tvQuestion,tvAnswer);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(tvQuestion.getText().toString())){
                    setNegativeToastMessage("Soru Seçiniz");
                    return;
                }
                if(TextUtils.isEmpty(tvAnswer.getText().toString())){
                    setNegativeToastMessage("Cevap Giriniz");
                    return;
                }
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.STORE_PROPERTY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                databaseReference
                                        .child(MenumConstant.USERS)
                                        .child(storePropertyPost.getUserName())
                                        .child("resetAnswer")
                                        .setValue(tvAnswer.getText().toString());
                                databaseReference
                                        .child(MenumConstant.USERS)
                                        .child(storePropertyPost.getUserName())
                                        .child("resetQuestion")
                                        .setValue(tvQuestion.getText().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                setPositiveToastMessage("Parola Sıfırlama sorusu ve cevabı güncellendi");
                dialog.dismiss();
            }
        });
    }

    private void enterResetAnswer(final String resetQuestion, final TextView tvQuestion, final TextView tvAnswer) {
        tvQuestion.setText("");
        tvAnswer.setText("");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_enter_reset_answer,null);
        Button btnExit=view.findViewById(R.id.btnExit);
        Button btnSave=view.findViewById(R.id.btnSave);
        final EditText etAnswer=view.findViewById(R.id.etAnswer);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etAnswer.getText().toString())){
                    Toast.makeText(Settings.this,"Cevap Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                tvQuestion.setText(resetQuestion);
                tvAnswer.setText(etAnswer.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void phoneSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_phone_settings,null);
        etPhone=view.findViewById(R.id.etPhone);
        Button btnSendCode=view.findViewById(R.id.btnSendCode);
        Button btnExit=view.findViewById(R.id.btnExit);
        builder.setView(view);
        dialogPhoneSettings=builder.create();
        dialogPhoneSettings.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialogPhoneSettings.show();
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPhoneSettings.dismiss();
            }
        });

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etPhone.getText().toString())){
                    etPhone.setError("Cep Numarası Giriniz");
                    return;
                }
                String check=etPhone.getText().toString().substring(0,3);
                if(check.equalsIgnoreCase("+90")){
                    databaseReference
                            .child(MenumConstant.PHONE_NUMBERS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(etPhone.getText().toString())){
                                        setNegativeToastMessage("Kayıtlı Cep Numarası");
                                    }else{
                                        startPhoneNumberVerification(etPhone.getText().toString());
                                        sendCode(etPhone.getText().toString());
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }else{
                    setNegativeToastMessage("Geçersiz İşlem.");
                }
            }
        });
    }

    private void userNameSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_username_settings,null);
        final EditText etUserName1=view.findViewById(R.id.etUserName1);
        final EditText etUserName2=view.findViewById(R.id.etUserName2);
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnExit=view.findViewById(R.id.btnExit);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etUserName1.getText().toString())){
                    etUserName1.setError("Lütfen Kullanıcı Adı Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etUserName2.getText().toString())){
                    etUserName2.setError("Lütfen Kullanıcı Adınızı Tekrar Giriniz");
                    return;
                }

                final String userName1=etUserName1.getText().toString();
                String userName2=etUserName2.getText().toString();
                if(userName1.equals(userName2)==false){
                    StyleableToast st=new StyleableToast(Settings.this,"Farklı Kullanıcı İsimleri", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                }else{
                    databaseReference
                            .child(MenumConstant.USERS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userName1)){
                                        setNegativeToastMessage("Kayıtlı Kullanıcı Adı");
                                    }else{
                                        databaseReference
                                                .child(MenumConstant.STORE)
                                                .child(phoneNumber)
                                                .child(MenumConstant.STORE_PROPERTY)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);

                                                        databaseReference
                                                                .child(MenumConstant.USERS)
                                                                .child(storePropertyPost.getUserName())
                                                                .child("userName")
                                                                .setValue(userName1);
                                                        databaseReference
                                                                .child(MenumConstant.STORE)
                                                                .child(phoneNumber)
                                                                .child(MenumConstant.STORE_PROPERTY)
                                                                .child("userName")
                                                                .setValue(userName1);

                                                        final DatabaseReference dbFrom=databaseReference.child(MenumConstant.USERS).child(storePropertyPost.getUserName());
                                                        final DatabaseReference dbTo=databaseReference.child(MenumConstant.USERS).child(userName1);

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
                                                                                    setPositiveToastMessage("Kullanıcı Adınız Değiştirildi");
                                                                                    dialog.dismiss();
                                                                                }
                                                                            },1500);
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

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

        });
    }

    private void accountSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_account_settings,null);
        final EditText etName=view.findViewById(R.id.etName);
        final EditText etSurname=view.findViewById(R.id.etSurname);
        final EditText etStoreName=view.findViewById(R.id.etStoreName);
        final EditText etAdress=view.findViewById(R.id.etAdress);
        final AutoCompleteTextView etCity=view.findViewById(R.id.etCity);
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnExit=view.findViewById(R.id.btnExit);

        String cities[]=getResources().getStringArray(R.array.cities);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        etCity.setAdapter(adapter);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.STORE_PROPERTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                        etName.setText(storePropertyPost.getName());
                        etSurname.setText(storePropertyPost.getSurname());
                        etAdress.setText(storePropertyPost.getAdress());
                        etCity.setText(storePropertyPost.getCity());
                        etStoreName.setText(storePropertyPost.getStoreName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                    etName.setError("Lütfen İsim Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etSurname.getText().toString())){
                    etSurname.setError("Lütfen Soyisim Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etStoreName.getText().toString())){
                    etStoreName.setError("Lütfen Mekan Adı Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etAdress.getText().toString())){
                    etAdress.setError("Lütfen Adres Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etCity.getText().toString())){
                    etCity.setError("Lütfen Şehir Giriniz");
                    return;
                }

                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.STORE_PROPERTY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                storePropertyPost.setAdress(etAdress.getText().toString());
                                storePropertyPost.setCity(etCity.getText().toString());
                                storePropertyPost.setName(etName.getText().toString());
                                storePropertyPost.setSurname(etSurname.getText().toString());
                                storePropertyPost.setStoreName(etStoreName.getText().toString());
                                storePropertyPost.setPassword(storePropertyPost.getPassword());
                                storePropertyPost.setUserName(storePropertyPost.getUserName());
                                storePropertyPost.setPhoneNumber(storePropertyPost.getPhoneNumber());
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.STORE_PROPERTY)
                                        .setValue(storePropertyPost);

                                databaseReference
                                        .child(MenumConstant.USERS)
                                        .child(storePropertyPost.getUserName())
                                        .child("storeName")
                                        .setValue(etStoreName.getText().toString());

                                setNegativeToastMessage("Güncelleme Tamamlandı");
                                dialog.dismiss();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });
    }

    private void passwordSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_password_settings,null);
        final EditText etPassword1=view.findViewById(R.id.etPassword1);
        etPassword1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        final EditText etPassword2=view.findViewById(R.id.etPassword2);
        etPassword2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        Button btnSave=view.findViewById(R.id.btnSave);
        Button btnExit=view.findViewById(R.id.btnExit);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etPassword1.getText().toString())){
                    etPassword1.setError("Lütfen Şifrenizi Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etPassword2.getText().toString())){
                    etPassword2.setError("Lütfen Şifrenizi Tekrar Giriniz");
                    return;
                }

                final String passsword1=etPassword1.getText().toString();
                String passsword2=etPassword2.getText().toString();
                if(passsword1.equals(passsword2)==false){
                    StyleableToast st=new StyleableToast(Settings.this,"Şifreler Farklı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                }else{
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.STORE_PROPERTY)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                                    databaseReference
                                            .child(MenumConstant.STORE)
                                            .child(phoneNumber)
                                            .child(MenumConstant.STORE_PROPERTY)
                                            .child("password")
                                            .setValue(passsword1);
                                    databaseReference
                                            .child(MenumConstant.USERS)
                                            .child(storePropertyPost.getUserName())
                                            .child("password")
                                            .setValue(passsword1);
                                    StyleableToast st=new StyleableToast(Settings.this,"Şifre Başarıyla Değiştirildi", Toast.LENGTH_LONG);
                                    st.setBackgroundColor(Color.parseColor("#0000ff"));
                                    st.setTextColor(Color.WHITE);
                                    st.setCornerRadius(2);
                                    st.show();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

        });
    }

    private void notificationSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        View view=LayoutInflater.from(Settings.this).inflate(R.layout.layout_notification_settings,null);
        final Switch swBeep=view.findViewById(R.id.swBeep);
        final Switch swLight=view.findViewById(R.id.swLight);
        final Switch swVibration=view.findViewById(R.id.swVibration);
        final Switch swCloseNotification=view.findViewById(R.id.swCloseNotification);
        Button btnExit=view.findViewById(R.id.btnExit);

        setCurrentNotificationSettings(swBeep,swLight,swVibration,swCloseNotification);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        swBeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swBeep.isChecked()){
                    typeNotification(1,swBeep,"closeBeep","Bildirim sesi kapatılsın mı ?");
                }else{
                    StyleableToast st=new StyleableToast(Settings.this,"Bildirim sesi açıldı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                            .child("closeBeep")
                            .setValue("false");
                }
            }
        });

        swLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swLight.isChecked()){
                    typeNotification(2,swLight,"closeLight","Bildirim ışığı kapatılsın mı ?");
                }else{
                    StyleableToast st=new StyleableToast(Settings.this,"Bildirim ışığı açıldı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                            .child("closeLight")
                            .setValue("false");
                }
            }
        });

        swVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swVibration.isChecked()){
                    typeNotification(3,swVibration,"closeVibration","Bildirim titreşimi kapatılsın mı ?");
                }else{
                    StyleableToast st=new StyleableToast(Settings.this,"Bildirim titreşimi açıldı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                            .child("closeVibration")
                            .setValue("false");
                }
            }
        });

        swCloseNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swCloseNotification.isChecked()){
                    typeNotification(4,swCloseNotification,"closeNotification","Bildirimler tamamen kapatılsın mı ?");
                }else{
                    StyleableToast st=new StyleableToast(Settings.this,"Bildirimler açıldı", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.NOTIFICATIONS)
                            .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                            .child("closeNotification")
                            .setValue("false");
                }
            }
        });
    }

    private void setCurrentNotificationSettings(final Switch swBeep, final Switch swLight, final Switch swVibration, final Switch swCloseNotification) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NewNotificationSettings newNotificationSettings=dataSnapshot.getValue(NewNotificationSettings.class);
                        if(newNotificationSettings.getCloseBeep().equalsIgnoreCase("false")){
                            swBeep.setChecked(false);
                        }else{
                            swBeep.setChecked(true);
                        }

                        if(newNotificationSettings.getCloseLight().equalsIgnoreCase("false")){
                            swLight.setChecked(false);
                        }else{
                            swLight.setChecked(true);
                        }

                        if(newNotificationSettings.getCloseVibration().equalsIgnoreCase("false")){
                            swVibration.setChecked(false);
                        }else{
                            swVibration.setChecked(true);
                        }

                        if(newNotificationSettings.getCloseNotification().equalsIgnoreCase("false")){
                            swCloseNotification.setChecked(false);
                        }else{
                            swCloseNotification.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void typeNotification(final int ID, final Switch whichSwitch, final String which, String confirmMessage) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(Settings.this);
        View viewConfirm=LayoutInflater.from(Settings.this).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        tvTitle.setText(confirmMessage);

        DisplayMetrics metrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth=metrics.widthPixels;
        int screenHeight=metrics.heightPixels;
        double wi=(double)screenWidth/(double)metrics.xdpi;
        double hi=(double)screenHeight/(double)metrics.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        final double screenInches = Math.sqrt(x+y);

        if (screenInches<5){
            tvTitle.setTextSize(18);
        }else if(screenInches>5 && screenInches<7){
            tvTitle.setTextSize(28);
        }else{
            tvTitle.setTextSize(38);
        }

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
                whichSwitch.setChecked(false);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION_SETTINGS)
                        .child(which)
                        .setValue("true");
                switch (ID){
                    case 1:
                        setNegativeToastMessage("Bildirim Sesi Kapatıldı");
                        break;
                    case 2:
                        setNegativeToastMessage("Bildirim Işığı Kapatıldı");
                        break;
                    case 3:
                        setNegativeToastMessage("Bildirim titeşimi Kapatıldı");
                        break;
                    case 4:
                        setNegativeToastMessage("Bildirimler Kapatıldı");
                        break;

                }
                dialog.dismiss();
            }
        });
    }

    private void setNegativeToastMessage(String message) {
        StyleableToast st=new StyleableToast(Settings.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff0000"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void setPositiveToastMessage(String message) {
        StyleableToast st=new StyleableToast(Settings.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void backgroundImageSettings() {
        new BackgroundImage(phoneNumber);
        startActivity(new Intent(Settings.this, BackgroundImage.class));
        finish();
    }

    private void sendCode(final String phoneNumber){
        AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Kayıt");

        final LayoutInflater layoutInflater=LayoutInflater.from(this);
        View view=layoutInflater.inflate(R.layout.layout_vertification_code,null);
        final EditText etVertificationCode=view.findViewById(R.id.etVertificationCode);
        Button btnConfirm=view.findViewById(R.id.btnConfirm);
        Button btnResendCode=view.findViewById(R.id.btnResendCode);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        dialogBuilder.setView(view);
        alertDialog_VertificationCode=dialogBuilder.create();
        alertDialog_VertificationCode.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=etVertificationCode.getText().toString();
                if(TextUtils.isEmpty(etVertificationCode.getText().toString())){
                    setNegativeToastMessage("Lütfen şifreyi giriniz");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(phoneNumber,mResendToken);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog_VertificationCode.dismiss();
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        Toast.makeText(this,"Doğrulama kodu gönderildi",Toast.LENGTH_SHORT).show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            alertDialog_VertificationCode.dismiss();
                            Toast.makeText(Settings.this,"Kayıt Tamamlandı",Toast.LENGTH_SHORT).show();
                            savePhoneSettings();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Settings.this,"Geçersiz Kod",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void savePhoneSettings() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.STORE_PROPERTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StorePropertyPost storePropertyPost=dataSnapshot.getValue(StorePropertyPost.class);
                        // users içinde telefon numarası değiştirildi
                        databaseReference
                                .child(MenumConstant.USERS)
                                .child(storePropertyPost.getUserName())
                                .child("phoneNumber")
                                .setValue(etPhone.getText().toString());

                        // store property içindeki telefon numarası değiştirildi
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.STORE_PROPERTY)
                                .child("phoneNumber")
                                .setValue(etPhone.getText().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // telefon kayıtlarının tutulduğu yere yeni telefon numarası eklendi ve eskiside silindi
                                databaseReference.child(MenumConstant.PHONE_NUMBERS).child(phoneNumber).removeValue();
                                databaseReference.child(MenumConstant.PHONE_NUMBERS).child(etPhone.getText().toString()).child(etPhone.getText().toString()).setValue("");

                                changeCustomersLikeDislike();
                                changeCustomerProductComments();
                                final DatabaseReference dbFrom=databaseReference.child(MenumConstant.STORE).child(phoneNumber);
                                final DatabaseReference dbTo=databaseReference.child(MenumConstant.STORE).child(etPhone.getText().toString());

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
                                                    },1500);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        },1500);
        dialogPhoneSettings.dismiss();
        setPositiveToastMessage("Cep Numaranız Başarıyla Güncellendi");
        setPositiveToastMessage("Tekrar giriş yapınız...");
        startActivity(new Intent(Settings.this,LoginActivity.class));
        finish();
    }

    private void changeCustomerProductComments() {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImei:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImei.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.PRODUCT_COMMENTS)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImei.getKey().toString())
                                                        .child(MenumConstant.PRODUCT_COMMENTS)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    final DatabaseReference dbFrom=databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImei.getKey().toString())
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(phoneNumber);
                                                                    final DatabaseReference dbTo=databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImei.getKey().toString())
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(etPhone.getText().toString());

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
                                                                                        },1500);
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

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast.makeText(Settings.this,"Doğrulama Kodu Talep Ediniz",Toast.LENGTH_SHORT).show();
        }

    }

    private void changeCustomersLikeDislike() {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImei:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImei.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.LIKE_DISLIKE)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImei.getKey().toString())
                                                        .child(MenumConstant.LIKE_DISLIKE)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    final DatabaseReference dbFrom=databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImei.getKey().toString())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(phoneNumber);
                                                                    final DatabaseReference dbTo=databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImei.getKey().toString())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(etPhone.getText().toString());

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
                                                                                        },1500);
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

    public static String getPhoneNumber() {
        return phoneNumber;
    }


}

package menum.menum.RegisterScreenPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import menum.menum.Constant.MenumConstant;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.Model.BackGroundImagePost;
import menum.menum.Model.StorePropertyPost;
import menum.menum.Model.UsersPost;
import menum.menum.R;

public class RegisterBoss extends AppCompatActivity {

    private ScrollView scr ;
    private EditText etPhone,etName,etSurname,etStoreName,etAdress,etUserName,etPassword1,etPassword2 ;
    private Button btnSave,btnSendCode;
    private TextView tvAnswer,tvQuestion;
    private AutoCompleteTextView etCity;
    private RadioButton rbFood,rbCity,rbBook;
    private DatabaseReference databaseReference;

    private ArrayAdapter<String> adapter ;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId;
    private static boolean isSucces=false;

    AlertDialog alertDialog_VertificationCode;

    private List<String> listPhoneNumber;
    private List<String> listUserName;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterBoss.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_boss);
        init();
        chechWhichResetQuestion();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(RegisterBoss.this,"Geçersiz Telefon Numarası",Toast.LENGTH_SHORT).show();
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

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etPhone.getText().toString())){
                    Snackbar.make(scr,"Lütfen cep numaranızı giriniz",Snackbar.LENGTH_SHORT).show();
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
                                        StyleableToast st=new StyleableToast(RegisterBoss.this,"Bu numara sistemde kayıtlı.",Toast.LENGTH_LONG);
                                        st.setBackgroundColor(Color.parseColor("#ff0000"));
                                        st.setTextColor(Color.WHITE);
                                        st.setCornerRadius(2);
                                        st.show();
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
                    StyleableToast st=new StyleableToast(RegisterBoss.this,"Geçersiz İşlem." + check,Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#ff0000"));
                    st.setTextColor(Color.WHITE);
                    st.setCornerRadius(2);
                    st.show();
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsEmpty();
            }
        });
    }

    private void init() {
        scr=findViewById(R.id.scr);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        btnSave=findViewById(R.id.btnSave);
        btnSendCode=findViewById(R.id.btnSendCode);
        tvAnswer=findViewById(R.id.tvAnswer);
        tvQuestion=findViewById(R.id.tvQuestion);

        rbBook=findViewById(R.id.rbBook);
        rbCity=findViewById(R.id.rbCity);
        rbFood=findViewById(R.id.rbFood);

        etPhone=findViewById(R.id.etPhone);
        etName=findViewById(R.id.etName);
        etSurname=findViewById(R.id.etSurname);
        etStoreName=findViewById(R.id.etStoreName);
        etAdress=findViewById(R.id.etAdress);
        etCity=findViewById(R.id.etCity);
        etUserName=findViewById(R.id.etUserName);
        etPassword1=findViewById(R.id.etPassword1);
        etPassword1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        etPassword2=findViewById(R.id.etPassword2);
        etPassword2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});

        String cities[]=getResources().getStringArray(R.array.cities);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        etCity.setAdapter(adapter);

        listPhoneNumber= new ArrayList<>();
        listUserName= new ArrayList<>();
    }

    private void chechWhichResetQuestion() {

        rbFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbFood.getText().toString());
            }
        });

        rbBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbBook.getText().toString());
            }
        });

        rbCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterResetAnswer(rbCity.getText().toString());
            }
        });
    }

    private void enterResetAnswer(final String resetQuestion) {
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
                    Toast.makeText(RegisterBoss.this,"Cevap Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                tvQuestion.setText(resetQuestion);
                tvAnswer.setText(etAnswer.getText().toString());
                dialog.dismiss();
            }
        });
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
                    Snackbar.make(scr,"Lütfen şifreyi giriniz",Snackbar.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterBoss.this,"Kayıt Tamamlandı",Toast.LENGTH_SHORT).show();
                            isSucces=true;
                            etPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick, 0);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(RegisterBoss.this,"Geçersiz Kod",Toast.LENGTH_SHORT).show();
                            }
                        }
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
            Toast.makeText(RegisterBoss.this,"Doğrulama Kodu Talep Ediniz",Toast.LENGTH_SHORT).show();
        }

    }

    private void checkIsEmpty() {
        if(TextUtils.isEmpty(etPhone.getText().toString())){
            etPhone.setError("Lütfen Cep Numaranızı Giriniz");
            return;
        }
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
        if(TextUtils.isEmpty(etUserName.getText().toString())){
            etUserName.setError("Lütfen Kullanıcı Adı Giriniz");
            return;
        }
        if(TextUtils.isEmpty(etPassword1.getText().toString())){
            etPassword1.setError("Lütfen Şifrenizi Giriniz");
            return;
        }
        if(TextUtils.isEmpty(etPassword2.getText().toString())){
            etPassword2.setError("Lütfen Şifrenizi Giriniz");
            return;
        }
        if(TextUtils.isEmpty(tvQuestion.getText().toString())){
            Toast.makeText(RegisterBoss.this,"Parola Sıfırlama Sorusu Seçiniz",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(tvAnswer.getText().toString())){
            Toast.makeText(RegisterBoss.this,"Parola Sıfırlama Cevabı Giriniz",Toast.LENGTH_SHORT).show();
            return;
        }
        if(etPassword1.getText().toString().equalsIgnoreCase(etPassword2.getText().toString())==false){
            etPassword1.setError("Şifreler Farklı");
            etPassword2.setError("Şifreler Farklı");
            etPassword1.setText("");
            etPassword2.setText("");
        }else{

            if(isSucces==false){
                etPhone.setError("Cep Numaranızı Doğrulayınız");
            }else{
                saveUserNameAndPassword();
            }
        }
    }

    private void saveUserNameAndPassword(){
        databaseReference
                .child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(etUserName.getText().toString())){
                            etUserName.setError("Kullanıcı Adı Geçersiz");
                            StyleableToast st=new StyleableToast(RegisterBoss.this,"Kayıtlı Kullanıcı Adı. Lütfen farklı bir kullanıcı adı giriniz",Toast.LENGTH_LONG);
                            st.setBackgroundColor(Color.parseColor("#ff0000"));
                            st.setTextColor(Color.WHITE);
                            st.setCornerRadius(2);
                            st.show();
                        }else{
                            try{
                                String phoneNumber=etPhone.getText().toString();
                                String userName=etUserName.getText().toString();
                                String password=etPassword1.getText().toString();
                                String storeName=etStoreName.getText().toString();
                                String resetAnswer=tvAnswer.getText().toString();
                                String resetQuestion=tvQuestion.getText().toString();
                                UsersPost usersPost= new UsersPost(phoneNumber,userName,password,storeName,resetQuestion,resetAnswer,"true");
                                databaseReference
                                        .child(MenumConstant.USERS)
                                        .child(userName)
                                        .setValue(usersPost);

                                usersPost.setPhoneNumber(phoneNumber);
                                databaseReference
                                        .child(MenumConstant.PHONE_NUMBERS)
                                        .child(phoneNumber)
                                        .child(phoneNumber)
                                        .setValue(usersPost.getPhoneNumber());

                                registerStore();
                            }catch (Exception ex){
                                Toast.makeText(RegisterBoss.this,"Kullanıcı isminiz '.', '#', '$', '[', ya da '] gibi karakterler içeremez.",Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void registerStore(){
        String phoneNumber=etPhone.getText().toString();
        String name=etName.getText().toString();
        String surname=etSurname.getText().toString();
        String storeName=etStoreName.getText().toString();
        String adress=etAdress.getText().toString();
        String city=etCity.getText().toString();
        String userName=etUserName.getText().toString();
        String password=etPassword1.getText().toString();
        StorePropertyPost storePropertyPost=
                new StorePropertyPost(phoneNumber,name,surname,storeName,adress,city,userName,password,"");
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.STORE_PROPERTY)
                .setValue(storePropertyPost);
        UsersPost usersPost=new UsersPost();
        usersPost.setIsActive("true");
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.CLOSED_ACCOUNTS)
                .child("isActive")
                .setValue(usersPost.getIsActive());

        BackGroundImagePost backGroundImagePost=new BackGroundImagePost();
        backGroundImagePost.setImage1("");
        backGroundImagePost.setImage2("");
        backGroundImagePost.setImage3("");
        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.BACKGROUND_IMAGES).setValue(backGroundImagePost);

        StyleableToast st=new StyleableToast(RegisterBoss.this,"Kayıt Tamamlandı.",Toast.LENGTH_LONG);
        st.setBackgroundColor(Color.parseColor("#3791CC"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.setIcon(R.drawable.ic_tick);
        st.show();

        try{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(RegisterBoss.this,LoginActivity.class));
                    finish();
                }
            },500);
        }catch (Exception e){

        }
    }
}

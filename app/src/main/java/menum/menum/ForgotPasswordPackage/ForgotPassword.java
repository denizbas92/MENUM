package menum.menum.ForgotPasswordPackage;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import menum.menum.Constant.MenumConstant;
import menum.menum.LoginScreenPackage.LoginActivity;
import menum.menum.Model.ForgotPasswordPost;
import menum.menum.Model.UsersPost;
import menum.menum.R;

public class ForgotPassword extends AppCompatActivity {

    private Button btnControl,btnSave;
    private TextView tvQuestion,tvAnswer;
    private RadioButton rbFood,rbCity,rbBook;
    private EditText etUserName;
    private EditText etPassword,etPasswordAgain;
    private TextInputLayout etP1,etP2;
    private DatabaseReference databaseReference;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        chechWhichResetQuestion();

        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etUserName.getText().toString())){
                    Toast.makeText(ForgotPassword.this,"Kullanıcı Adı Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(tvQuestion.getText().toString())){
                    Toast.makeText(ForgotPassword.this,"Soru Seçiniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(tvAnswer.getText().toString())){
                    Toast.makeText(ForgotPassword.this,"Cevap Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                control(tvQuestion.getText().toString(),tvAnswer.getText().toString());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etPassword.getText().toString())){
                    etPassword.setError("Parola Giriniz");
                    return;
                }
                if(TextUtils.isEmpty(etPasswordAgain.getText().toString())){
                    etPasswordAgain.setError("Parola Giriniz");
                    return;
                }
                String pass1=etPassword.getText().toString();
                String pass2=etPasswordAgain.getText().toString();
                if(pass1.equalsIgnoreCase(pass2)){
                    databaseReference
                            .child(MenumConstant.USERS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(etUserName.getText().toString())){
                                        databaseReference
                                                .child(MenumConstant.USERS)
                                                .child(etUserName.getText().toString())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        UsersPost usersPost=dataSnapshot.getValue(UsersPost.class);
                                                        String phoneNumber=usersPost.getPhoneNumber();
                                                        usersPost.setPassword(etPassword.getText().toString());
                                                        databaseReference
                                                                .child(MenumConstant.USERS)
                                                                .child(etUserName.getText().toString())
                                                                .child("password")
                                                                .setValue(usersPost.getPassword());
                                                        databaseReference
                                                                .child(MenumConstant.STORE)
                                                                .child(phoneNumber)
                                                                .child(MenumConstant.STORE_PROPERTY)
                                                                .child("password")
                                                                .setValue(usersPost.getPassword());
                                                        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                                                        finish();
                                                        Toast.makeText(ForgotPassword.this,"Şifre Başarıyla Değiştirildi",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ForgotPassword.this,"Şifreler Farklı",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        btnControl=findViewById(R.id.btnControl);
        btnSave=findViewById(R.id.btnSave);
        tvQuestion=findViewById(R.id.tvQuestion);
        tvAnswer=findViewById(R.id.tvAnswer);
        rbFood=findViewById(R.id.rbFood);
        rbCity=findViewById(R.id.rbCity);
        rbBook=findViewById(R.id.rbBook);
        etUserName=findViewById(R.id.etUserName);
        etPassword=findViewById(R.id.etPassword);
        etPassword.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        etPasswordAgain=findViewById(R.id.etPasswordAgain);
        etPasswordAgain.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        etP1=findViewById(R.id.etP1);
        etP2=findViewById(R.id.etP2);
        databaseReference= FirebaseDatabase.getInstance().getReference();
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
        if(TextUtils.isEmpty(etUserName.getText().toString())){
            Toast.makeText(ForgotPassword.this,"Kullanıcı Adınızı Giriniz",Toast.LENGTH_SHORT).show();
            return;
        }
        tvQuestion.setText("");
        tvAnswer.setText("");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_enter_reset_answer,null);
        Button btnExit=view.findViewById(R.id.btnExit);
        Button btnSave=view.findViewById(R.id.btnSave);
        final EditText etAnswer=view.findViewById(R.id.etAnswer);
        btnSave.setText("SIFIRLA");
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
                    Toast.makeText(ForgotPassword.this,"Cevap Giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                tvQuestion.setText(resetQuestion);
                tvAnswer.setText(etAnswer.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void control(final String resetQuestion, final String resetAnswer) {
        databaseReference
                .child(MenumConstant.USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(etUserName.getText().toString())){
                            databaseReference
                                    .child(MenumConstant.USERS)
                                    .child(etUserName.getText().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ForgotPasswordPost forgotPasswordPost=dataSnapshot.getValue(ForgotPasswordPost.class);
                                            String question=forgotPasswordPost.getResetQuestion();
                                            if(question.equalsIgnoreCase(resetQuestion)){
                                                String answer=forgotPasswordPost.getResetAnswer();
                                                if(answer.equalsIgnoreCase(resetAnswer)){
                                                    etPasswordAgain.setVisibility(View.VISIBLE);
                                                    etPassword.setVisibility(View.VISIBLE);
                                                    btnSave.setVisibility(View.VISIBLE);
                                                    etP1.setVisibility(View.VISIBLE);
                                                    etP2.setVisibility(View.VISIBLE);
                                                }else{
                                                    Toast.makeText(ForgotPassword.this,"Soru veya Cevap Yanlış",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(ForgotPassword.this,"Soru veya Cevap Yanlış",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else{
                            Toast.makeText(ForgotPassword.this,"Geçersiz Kullanıcı Adı",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.Model.AdviceComplaintPost;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.AdviceComplaintAdapter;

public class ShowAdviceComplaint extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recAdviceComplaint;
    private LinearLayoutManager linearLayoutManager;
    AdviceComplaintAdapter adviceComplaintAdapter;

    private static String phoneNumber;

    private List<AdviceComplaintPost> adviceComplaintPostList;

    private DatabaseReference databaseReference;
    private static int counterAdvice;

    public ShowAdviceComplaint(){

    }

    public ShowAdviceComplaint(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShowAdviceComplaint.this,MainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_advice_complaint);
        init();
        getAdviceComplaint();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowAdviceComplaint.this,MainScreen.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        recAdviceComplaint=findViewById(R.id.recAdviceComplaint);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recAdviceComplaint.setLayoutManager(linearLayoutManager);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        adviceComplaintPostList=new ArrayList<>();

    }

    private void getAdviceComplaint() {
        adviceComplaintPostList.clear();
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.ADVICE_COMPLAINT)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.ADVICE_COMPLAINT)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (final DataSnapshot snapDate:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.ADVICE_COMPLAINT)
                                                        .child(snapDate.getKey().toString())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot snapHour:dataSnapshot.getChildren()){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.ADVICE_COMPLAINT)
                                                                            .child(snapDate.getKey().toString())
                                                                            .child(snapHour.getKey().toString())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    AdviceComplaintPost adviceComplaintPost=dataSnapshot.getValue(AdviceComplaintPost.class);
                                                                                    adviceComplaintPostList.add(adviceComplaintPost);
                                                                                    adviceComplaintAdapter=new AdviceComplaintAdapter(
                                                                                            ShowAdviceComplaint.this,
                                                                                            phoneNumber,
                                                                                            adviceComplaintPostList);
                                                                                    recAdviceComplaint.setAdapter(adviceComplaintAdapter);
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
                            StyleableToast st=new StyleableToast(ShowAdviceComplaint.this,"Yeni Bir Mesajınız Bulunmamaktadır..", Toast.LENGTH_LONG);
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
}

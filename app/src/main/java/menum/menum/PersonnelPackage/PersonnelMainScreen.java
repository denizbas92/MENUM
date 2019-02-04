package menum.menum.PersonnelPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.Model.AddPersonnelPost;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.PersonnelMainScreenAdapter;

public class PersonnelMainScreen extends AppCompatActivity {

    private Button btnAdd,btnBack;
    private RecyclerView recPersonnel;
    private PersonnelMainScreenAdapter personnelMainScreenAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static String phoneNumber ;
    private List<AddPersonnelPost> addPersonnelPostList;
    private DatabaseReference databaseReference;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PersonnelMainScreen.this, MainScreen.class));
        finish();
    }

    public PersonnelMainScreen(){

    }

    public PersonnelMainScreen(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_main_screen);
        init();
        getPersonnels();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonnelMainScreen.this, MainScreen.class));
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddPersonnel(phoneNumber);
                startActivity(new Intent(PersonnelMainScreen.this, AddPersonnel.class));
                finish();
            }
        });
    }

    private void init() {
        btnAdd=findViewById(R.id.btnAdd);
        btnBack=findViewById(R.id.btnBack);
        recPersonnel=findViewById(R.id.recPersonnel);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recPersonnel.setLayoutManager(linearLayoutManager);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        addPersonnelPostList=new ArrayList<>();
    }

    private void getPersonnels() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNELS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapID:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PERSONNELS)
                                    .child(snapID.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                            addPersonnelPostList.add(addPersonnelPost);
                                            personnelMainScreenAdapter=new PersonnelMainScreenAdapter(PersonnelMainScreen.this,phoneNumber,addPersonnelPostList);
                                            recPersonnel.setAdapter(personnelMainScreenAdapter);
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

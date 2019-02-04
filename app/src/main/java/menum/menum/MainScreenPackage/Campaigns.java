package menum.menum.MainScreenPackage;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import menum.menum.Model.CampaignPost;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.CampaignAdapter;

public class Campaigns extends AppCompatActivity {

    private RecyclerView recCampaign;
    private LinearLayoutManager linearLayoutManager;
    private CampaignAdapter campaignAdapter;

    private Button btnBack;
    private FloatingActionButton fabAdd;

    private static String phoneNumber ;
    private static String userName;

    private List<CampaignPost> listCampaignPost;
    private CampaignPost campaignPost;

    private DatabaseReference databaseReference;

    public Campaigns() {
    }

    public Campaigns(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Campaigns.this,MainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaigns);

        init();
        setCampaigns();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Campaigns.this,MainScreen.class));
                finish();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCampaign();
            }
        });
    }

    private void init() {
        recCampaign=findViewById(R.id.recCampaign);
        btnBack=findViewById(R.id.btnBack);
        fabAdd=findViewById(R.id.fabAdd);

        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recCampaign.setLayoutManager(linearLayoutManager);
        listCampaignPost =new ArrayList<>();
        campaignPost =new CampaignPost();

        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void setCampaigns() {
        listCampaignPost.clear();
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.CAMPAIGN)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.CAMPAIGN)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(final DataSnapshot snapDate:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.CAMPAIGN)
                                                        .child(snapDate.getKey().toString())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapHour:dataSnapshot.getChildren()){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.CAMPAIGN)
                                                                            .child(snapDate.getKey().toString())
                                                                            .child(snapHour.getKey().toString())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    CampaignPost campaignPost=dataSnapshot.getValue(CampaignPost.class);
                                                                                    listCampaignPost.add(campaignPost);
                                                                                    campaignAdapter=new CampaignAdapter(Campaigns.this,phoneNumber, listCampaignPost);
                                                                                    recCampaign.setAdapter(campaignAdapter);
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
                            StyleableToast st=new StyleableToast(Campaigns.this,"Eklenmiş Kampanyanız Bulunmamaktadır.", Toast.LENGTH_SHORT);
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

    private void addCampaign() {
        new AddCampaign(phoneNumber);
        startActivity(new Intent(Campaigns.this,AddCampaign.class));
        finish();
    }
}

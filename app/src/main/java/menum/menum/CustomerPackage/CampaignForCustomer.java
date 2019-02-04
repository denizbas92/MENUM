package menum.menum.CustomerPackage;

import android.content.Intent;
import android.graphics.Color;
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
import menum.menum.CustomerAdapterPackage.CampaignForCustomerAdapter;
import menum.menum.MainScreenPackage.Settings;
import menum.menum.Model.CampaignPost;
import menum.menum.R;

public class CampaignForCustomer extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recCA;
    private LinearLayoutManager linearLayoutManager;

    private static String phoneNumber;
    private static  String storeName;

    private List<CampaignPost> listCampaignPost;
    private CampaignForCustomerAdapter campaignForCustomerAdapter;

    private DatabaseReference databaseReference;
    private List<String> listStorePhoneNumbers;

    public CampaignForCustomer(){

    }

    public CampaignForCustomer(String phoneNumber, String storeName) {
        this.phoneNumber=phoneNumber;
        this.storeName=storeName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CampaignForCustomer.this,CustomerMainScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_for_customer);
        init();
        getCampaignFromStore();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CampaignForCustomer.this,CustomerMainScreen.class));
                finish();
            }
        });
    }

    private void init() {
        btnBack=findViewById(R.id.btnBack);
        recCA=findViewById(R.id.recCA);
        linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recCA.setLayoutManager(linearLayoutManager);

        listCampaignPost=new ArrayList<>();
        listStorePhoneNumbers=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    private void getCampaignFromStore() {
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
                                                                for(DataSnapshot snapHour:dataSnapshot.getChildren()){
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
                                                                                    campaignForCustomerAdapter=new CampaignForCustomerAdapter(
                                                                                            CampaignForCustomer.this,
                                                                                            phoneNumber,
                                                                                            storeName,
                                                                                            listCampaignPost
                                                                                    );

                                                                                    recCA.setAdapter(campaignForCustomerAdapter);
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
                            setNegativeToastMessage("Kampanya/Duyuru Bulunamadı");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setNegativeToastMessage(String message) {
        StyleableToast st=new StyleableToast(CampaignForCustomer.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#ff0000"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }

    private void setPositiveToastMessage(String message) {
        StyleableToast st=new StyleableToast(CampaignForCustomer.this,message, Toast.LENGTH_SHORT);
        st.setBackgroundColor(Color.parseColor("#0000ff"));
        st.setTextColor(Color.WHITE);
        st.setCornerRadius(2);
        st.show();
    }
}

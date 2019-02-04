package menum.menum.CustomerAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import menum.menum.CustomerPackage.CampaignForCustomer;
import menum.menum.Model.CampaignPost;
import menum.menum.R;

/**
 * Created by deniz on 8.2.2018.
 */

public class CampaignForCustomerAdapter extends RecyclerView.Adapter<CampaignForCustomerAdapter.MyViewHolder> {
    Context context;
    private static String phoneNumber;
    private static String storeName;
    private List<CampaignPost> listCampaignPost;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public CampaignForCustomerAdapter(CampaignForCustomer campaignForCustomer, String phoneNumber,String storeName, List<CampaignPost> listCampaignPost) {
        this.context=campaignForCustomer;
        this.phoneNumber=phoneNumber;
        this.storeName=storeName;
        this.listCampaignPost=listCampaignPost;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_campaign,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.btnDelete.setVisibility(View.INVISIBLE);
        holder.tvOrder.setText(Integer.toString(position+1)+".");
        holder.tvTitle.setText(listCampaignPost.get(position).getTitle());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailCampaign(position);
            }
        });
    }

    private void detailCampaign(int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1=LayoutInflater.from(context).inflate(R.layout.layout_customer_campaign_detail,null);
        final TextView tvCampaignTitle=view1.findViewById(R.id.tvCampaignTitle);
        final TextView tvCampaignContent=view1.findViewById(R.id.tvCampaignContent);
        TextView tvStoreName=view1.findViewById(R.id.tvStoreName);
        tvStoreName.setText(storeName);
        TextView tvPublishDate=view1.findViewById(R.id.tvPublishDate);
        TextView tvPublishHour=view1.findViewById(R.id.tvPublishHour);
        Button btnExit=view1.findViewById(R.id.btnExit);

        tvCampaignContent.setText(listCampaignPost.get(position).getCampaignDetail());
        tvCampaignTitle.setText(listCampaignPost.get(position).getTitle());
        tvPublishHour.setText(listCampaignPost.get(position).getPublishHour());
        tvPublishDate.setText(listCampaignPost.get(position).getPublishDate());

        builder.setView(view1);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCampaignPost.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvOrder,tvTitle;
        Button btnDelete,btnDetail;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvOrder=itemView.findViewById(R.id.tvOrder);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnDetail=itemView.findViewById(R.id.btnDetail);
        }
    }
}

package menum.menum.StoreAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.Campaigns;
import menum.menum.Model.CampaignPost;
import menum.menum.R;
/**
 * Created by deniz on 19.1.2018.
 */
public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber;
    private List<CampaignPost> listCampaignPost;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public CampaignAdapter(Campaigns campaigns, String phoneNumber, List<CampaignPost> listCampaignPost) {
        this.context=campaigns;
        this.phoneNumber=phoneNumber;
        this.listCampaignPost = listCampaignPost;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_campaign,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvOrder.setText(Integer.toString(position+1)+".");
        holder.tvTitle.setText(listCampaignPost.get(position).getTitle());
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailCampaign(position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCampaign(position);
            }
        });
    }

    private void detailCampaign(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1=LayoutInflater.from(context).inflate(R.layout.layout_campaign_detail,null);
        final EditText etCampaignTitle=view1.findViewById(R.id.etCampaignTitle);
        final EditText etCampaignContent=view1.findViewById(R.id.etCampaignContent);
        TextView tvPublishDate=view1.findViewById(R.id.tvPublishDate);
        TextView tvPublishHour=view1.findViewById(R.id.tvPublishHour);
        final Button btnEditSave=view1.findViewById(R.id.btnEditSave);
        Button btnExit=view1.findViewById(R.id.btnExit);

        etCampaignContent.setText(listCampaignPost.get(position).getCampaignDetail());
        etCampaignTitle.setText(listCampaignPost.get(position).getTitle());
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

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEditSave.getText().toString().equalsIgnoreCase("DÜZENLE")){
                    btnEditSave.setText("KAYDET");
                    etCampaignContent.setEnabled(true);
                    etCampaignTitle.setEnabled(true);
                }else{
                    if(TextUtils.isEmpty(etCampaignTitle.getText().toString())){
                        StyleableToast st=new StyleableToast(context,"Kampanya Başlığı Giriniz", Toast.LENGTH_SHORT);
                        st.setBackgroundColor(Color.parseColor("#ff0000"));
                        st.setTextColor(Color.WHITE);
                        st.setCornerRadius(2);
                        st.show();
                        return;
                    }
                    if(TextUtils.isEmpty(etCampaignContent.getText().toString())){
                        StyleableToast st=new StyleableToast(context,"Kampanya İçeriği Giriniz", Toast.LENGTH_SHORT);
                        st.setBackgroundColor(Color.parseColor("#ff0000"));
                        st.setTextColor(Color.WHITE);
                        st.setCornerRadius(2);
                        st.show();
                        return;
                    }

                    String title=etCampaignTitle.getText().toString();
                    String content=etCampaignContent.getText().toString();

                    CampaignPost campaignPost=new CampaignPost(title,content,listCampaignPost.get(position).getPublishDate(),listCampaignPost.get(position).getPublishHour());

                    databaseReference
                            .child(MenumConstant.STORE)
                            .child(phoneNumber)
                            .child(MenumConstant.CAMPAIGN)
                            .child(listCampaignPost.get(position).getPublishDate())
                            .child(listCampaignPost.get(position).getPublishHour())
                            .setValue(campaignPost);

                    StyleableToast st=new StyleableToast(context,"Kampanya Başarıyla Güncellendi", Toast.LENGTH_SHORT);
                    st.setBackgroundColor(Color.parseColor("#0000ff"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_tick);
                    st.setCornerRadius(2);
                    st.show();

                    dialog.dismiss();
                    context.startActivity(new Intent(context,Campaigns.class));
                }
            }
        });
    }

    private void deleteCampaign(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=view1.findViewById(R.id.tvTitle);
        tvTitle.setText("Kampanyanız Silinecek. Bunu Onaylıyor musunuz ?");
        Button btnConfirm=view1.findViewById(R.id.btnConfirm);
        Button btnCancel=view1.findViewById(R.id.btnCancel);

        builder.setView(view1);
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
                        .child(MenumConstant.CAMPAIGN)
                        .child(listCampaignPost.get(position).getPublishDate())
                        .child(listCampaignPost.get(position).getPublishHour())
                        .removeValue();

                StyleableToast st=new StyleableToast(context,"Kampanya Silindi", Toast.LENGTH_SHORT);
                st.setBackgroundColor(Color.parseColor("#0000ff"));
                st.setTextColor(Color.WHITE);
                st.setIcon(R.drawable.ic_tick);
                st.setCornerRadius(2);
                st.show();

                dialog.dismiss();
                context.startActivity(new Intent(context,Campaigns.class));

            }
        });
    }

    @Override
    public int getItemCount() {
        return listCampaignPost.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

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

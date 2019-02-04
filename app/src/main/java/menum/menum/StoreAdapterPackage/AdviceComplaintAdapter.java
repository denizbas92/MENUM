package menum.menum.StoreAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.ShowAdviceComplaint;
import menum.menum.Model.AdviceComplaintPost;
import menum.menum.R;

/**
 * Created by deniz on 7.2.2018.
 */

public class AdviceComplaintAdapter extends RecyclerView.Adapter<AdviceComplaintAdapter.MyViewHolder>  {

    Context context;

    private static String phoneNumber;
    private static List<AdviceComplaintPost> adviceComplaintPostList ;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public AdviceComplaintAdapter(ShowAdviceComplaint showAdviceComplaint, String phoneNumber, List<AdviceComplaintPost> adviceComplaintPostList) {
        this.context=showAdviceComplaint;
        this.phoneNumber=phoneNumber;
        this.adviceComplaintPostList=adviceComplaintPostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_advice_complaint,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvDate.setText(adviceComplaintPostList.get(position).getDate());
        holder.tvHour.setText(adviceComplaintPostList.get(position).getHour());
        holder.tvAdviceTitle.setText(adviceComplaintPostList.get(position).getTitle());

        if(adviceComplaintPostList.get(position).getSeen().equalsIgnoreCase("false")){
            holder.tvSeen.setText("Okunmadı");
        }else{
            holder.tvSeen.setText("Okundu");
            holder.tvSeen.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_tick,0);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAdviceComplaint(holder,position);
            }
        });

        holder.btnReadAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readAdvice(holder,position);
            }
        });
    }

    private void readAdvice(MyViewHolder holder, int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.layout_message,null);
        TextView tvMessage=view.findViewById(R.id.tvMessage);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        tvMessage.setText(adviceComplaintPostList.get(position).getContent());

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.ADVICE_COMPLAINT)
                .child(adviceComplaintPostList.get(position).getDate())
                .child(adviceComplaintPostList.get(position).getHour())
                .child("seen")
                .setValue("true");

        holder.tvSeen.setText("Okundu");
        holder.tvSeen.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_tick,0);
    }

    private void deleteAdviceComplaint(MyViewHolder holder, final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=view.findViewById(R.id.tvTitle);
        Button btnConfirm=view.findViewById(R.id.btnConfirm);
        Button btnCancel=view.findViewById(R.id.btnCancel);

        tvTitle.setText("Müşterinizden gelen mesaj silinecek. Bunu onaylıyor musunuz ?");

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.ADVICE_COMPLAINT)
                        .child(adviceComplaintPostList.get(position).getDate())
                        .child(adviceComplaintPostList.get(position).getHour())
                        .removeValue();
                dialog.dismiss();
                context.startActivity(new Intent(context,ShowAdviceComplaint.class));
                StyleableToast st=new StyleableToast(context,"Mesaj Silindi.", Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#0000ff"));
                st.setTextColor(Color.WHITE);
                st.setCornerRadius(2);
                st.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return adviceComplaintPostList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate,tvHour,tvAdviceTitle,tvSeen;
        Button btnDelete,btnReadAdvice;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvHour=itemView.findViewById(R.id.tvHour);
            tvAdviceTitle=itemView.findViewById(R.id.tvAdviceTitle);
            tvSeen=itemView.findViewById(R.id.tvSeen);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnReadAdvice=itemView.findViewById(R.id.btnReadAdvice);
        }
    }
}

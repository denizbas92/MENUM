package menum.menum.StoreAdapterPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.MainScreenPackage.MainScreen;
import menum.menum.MainScreenPackage.ShowAdviceComplaint;
import menum.menum.MenuCommentPackage.ProductCommentDetail;
import menum.menum.Model.NewNotificationPost;
import menum.menum.PersonnelPackage.PersonnelMainScreen;
import menum.menum.R;

/**
 * Created by deniz on 8.2.2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private List<NewNotificationPost> newNotificationPostList;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    public NotificationAdapter(MainScreen mainScreen, String phoneNumber, List<NewNotificationPost> newNotificationPostList) {
        this.context=mainScreen;
        this.phoneNumber=phoneNumber;
        this.newNotificationPostList=newNotificationPostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int index=newNotificationPostList.size()-1-position;
        if(newNotificationPostList.get(index).getSeen().equalsIgnoreCase("false")){
            holder.tvMessage.setText(newNotificationPostList.get(index).getNotification());
            holder.tvHour.setText(newNotificationPostList.get(index).getHour());
            holder.tvDate.setText(newNotificationPostList.get(index).getDate());
            holder.topLayout.setBackgroundColor(Color.parseColor("#0beaed"));
        }else{
            holder.tvMessage.setText(newNotificationPostList.get(index).getNotification());
            holder.tvHour.setText(newNotificationPostList.get(index).getHour());
            holder.tvDate.setText(newNotificationPostList.get(index).getDate());
            holder.topLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        setAllSeenTrue();

        holder.cbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification(holder,index);
            }
        });

        holder.cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newNotificationPostList.get(index).getCodeNot().equalsIgnoreCase("1")){
                    new PersonnelMainScreen(phoneNumber);
                    context.startActivity(new Intent(context,PersonnelMainScreen.class));
                }else if(newNotificationPostList.get(index).getCodeNot().equalsIgnoreCase("2")){
                    new ShowAdviceComplaint(phoneNumber);
                    context.startActivity(new Intent(context,ShowAdviceComplaint.class));
                }else if(newNotificationPostList.get(index).getCodeNot().equalsIgnoreCase("3")){
                    new ProductCommentDetail(phoneNumber,newNotificationPostList.get(index).getMenuCategoryName(),newNotificationPostList.get(index).getProductName());
                    context.startActivity(new Intent(context,ProductCommentDetail.class));
                }
            }
        });
    }

    private void deleteNotification(final MyViewHolder holder, final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        Button btnConfirm=view.findViewById(R.id.btnConfirm);
        TextView tvTitle=view.findViewById(R.id.tvTitle);
        tvTitle.setText("Bildirim silinecek. Bunu onaylıyor musunuz?");
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                holder.cbDelete.setChecked(false);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.NOTIFICATIONS)
                        .child(MenumConstant.NEW_NOTIFICATION)
                        .child(newNotificationPostList.get(position).getDate())
                        .child(newNotificationPostList.get(position).getHour())
                        .removeValue();
                dialog.dismiss();
                StyleableToast st=new StyleableToast(context,"Bildirim Silindi", Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#0000ff"));
                st.setTextColor(Color.WHITE);
                st.setCornerRadius(2);
                st.show();
            }
        });
    }

    private void setAllSeenTrue() {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.NOTIFICATIONS)
                .child(MenumConstant.NEW_NOTIFICATION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapDate:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.NOTIFICATIONS)
                                    .child(MenumConstant.NEW_NOTIFICATION)
                                    .child(snapDate.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapHour:dataSnapshot.getChildren()){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.NOTIFICATIONS)
                                                        .child(MenumConstant.NEW_NOTIFICATION)
                                                        .child(snapDate.getKey().toString())
                                                        .child(snapHour.getKey().toString())
                                                        .child("seen")
                                                        .setValue("true");
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

    @Override
    public int getItemCount() {
        return newNotificationPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate,tvHour,tvMessage ;
        CheckBox cbDelete;
        LinearLayout topLayout;
        CardView cardNotification;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvHour=itemView.findViewById(R.id.tvHour);
            tvMessage=itemView.findViewById(R.id.tvMessage);
            cbDelete=itemView.findViewById(R.id.cbDelete);
            topLayout=itemView.findViewById(R.id.topLayout);
            cardNotification=itemView.findViewById(R.id.cardNotification);
        }
    }
}

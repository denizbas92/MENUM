package menum.menum.CustomerAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.Model.CommentPost;
import menum.menum.R;

/**
 * Created by deniz on 6.2.2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    private  List<CommentPost> commentPostList;
    private static String phoneNumber;
    private static String menuCategoryName ;
    private static String productName;
    private static String imeiNumber;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public CommentAdapter(ProductDetailCustomer productDetailCustomer, String phoneNumber, String menuCategoryName, String productName,
                          String imeiNumber, List<CommentPost> commentPostList) {
        this.context=productDetailCustomer;
        this.commentPostList=commentPostList;
        this.imeiNumber=imeiNumber;
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvDate.setText(commentPostList.get(position).getDate());
        holder.tvHour.setText(commentPostList.get(position).getHour());
        holder.tvNameSurname.setText(commentPostList.get(position).getName()+" " + commentPostList.get(position).getSurname());
        holder.etComment.setText(commentPostList.get(position).getComment());
        if(commentPostList.get(position).getImeiNumber().equalsIgnoreCase(imeiNumber)){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
        }else{
            holder.btnDelete.setVisibility(View.INVISIBLE);
            holder.btnEdit.setVisibility(View.INVISIBLE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btnDelete.getText().toString().equalsIgnoreCase("İptal")){
                    holder.etComment.setEnabled(false);
                    holder.btnDelete.setText("Sil");
                    holder.btnEdit.setText("Düzenle");
                }else{
                    deleteComment(position);
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btnEdit.getText().toString().equalsIgnoreCase("Kaydet")){
                    editComment(holder,position);
                }else{
                    holder.etComment.setEnabled(true);
                    holder.btnEdit.setText("Kaydet");
                    holder.btnDelete.setText("İptal");
                }
            }
        });
    }

    private void editComment(MyViewHolder holder, int position) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(imeiNumber)
                .child(MenumConstant.PRODUCT_COMMENTS)
                .child(menuCategoryName)
                .child(productName)
                .child(commentPostList.get(position).getUID())
                .child("comment")
                .setValue(holder.etComment.getText().toString());
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PRODUCT_COMMENTS)
                .child(menuCategoryName)
                .child(productName)
                .child(commentPostList.get(position).getUID())
                .child("comment")
                .setValue(holder.etComment.getText().toString());
        Toast.makeText(context,"Yorumunuz Güncellendi.",Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context,ProductDetailCustomer.class));
    }

    private void deleteComment(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        tvTitle.setText("Yorumunuz silinsin mi ?");
        tvTitle.setTextSize(18);
        Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
        Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
        builder.setView(viewConfirm);
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
                        .child(MenumConstant.CUSTOMERS)
                        .child(imeiNumber)
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(commentPostList.get(position).getUID())
                        .removeValue();
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(commentPostList.get(position).getUID())
                        .removeValue();
                Toast.makeText(context,"Yorumunuz Silindi.",Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context,ProductDetailCustomer.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDate,tvHour,tvNameSurname;
        private EditText etComment;
        private Button btnReply,btnSeeAllComment,btnDelete,btnEdit;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvHour=itemView.findViewById(R.id.tvHour);
            tvNameSurname=itemView.findViewById(R.id.tvNameSurname);
            etComment=itemView.findViewById(R.id.etComment);
            btnReply=itemView.findViewById(R.id.btnReply);
            btnSeeAllComment=itemView.findViewById(R.id.btnSeeAllComment);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnEdit=itemView.findViewById(R.id.btnEdit);
        }
    }
}

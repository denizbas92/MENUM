package menum.menum.MenuCommentPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import menum.menum.Constant.MenumConstant;
import menum.menum.Model.CommentPost;
import menum.menum.R;

/**
 * Created by deniz on 9.2.2018.
 */

public class ProductCommentDetailAdapter extends RecyclerView.Adapter<ProductCommentDetailAdapter.MyViewHolder>{

    Context context;
    private static String phoneNumber ;
    private static String menuCategoryName;
    private static String productName ;
    private static String imeiNumber;
    private List<CommentPost> commentPostList;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    private static final String baseUrlHour="https://www.timeanddate.com/";
    private static String hour;
    private static String date;

    public ProductCommentDetailAdapter(ProductCommentDetail productCommentDetail, String phoneNumber, String menuCategoryName,
                                       String productName, List<CommentPost> commentPostList,String imeiNumber) {
        this.context=productCommentDetail;
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.productName=productName;
        this.commentPostList=commentPostList;
        this.imeiNumber=imeiNumber;
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
        holder.btnReply.setVisibility(View.VISIBLE);
        if(commentPostList.get(position).getImeiNumber().equalsIgnoreCase(imeiNumber)){
            holder.btnEdit.setVisibility(View.VISIBLE);
        }else{
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

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyComment(holder,position);
            }
        });
    }

    private void replyComment(MyViewHolder holder, int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_reply_comment,null);
        final EditText etReplyMessage=viewConfirm.findViewById(R.id.etReplyMessage);
        Button btnSend=viewConfirm.findViewById(R.id.btnSend);
        Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
        builder.setView(viewConfirm);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();
        etReplyMessage.setText("Sayın " + commentPostList.get(position).getName()+" " + commentPostList.get(position).getSurname());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etReplyMessage.getText().toString())){
                    etReplyMessage.setError("Cevap Giriniz");
                    return;
                }
                new GetTimeFromNet(context,etReplyMessage.getText().toString()).execute();
                dialog.dismiss();
            }
        });
    }

    private void editComment(MyViewHolder holder, int position) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(imeiNumber)
                .child(MenumConstant.PRODUCT_COMMENTS)
                .child(phoneNumber)
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
        context.startActivity(new Intent(context,ProductCommentDetail.class));
    }

    private void deleteComment(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        tvTitle.setText("Yorum silinsin mi ?");
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
                        .child(commentPostList.get(position).getImeiNumber())
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(phoneNumber)
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
                Toast.makeText(context,"Yorum Silindi." ,Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context,ProductCommentDetail.class));
            }
        });
    }

    public String getCurrentDate() {

        Calendar calendar ;
        SimpleDateFormat simpleDateFormat ;
        String date ;

        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        date=simpleDateFormat.format(calendar.getTime());
        return date;
    }

    public String getCurrentTime() {
        Calendar calendar ;
        SimpleDateFormat simpleDateFormat ;
        String time ;

        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("HH:mm");
        time=simpleDateFormat.format(calendar.getTime());
        return time;
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

    public class GetTimeFromNet extends AsyncTask<Void,Void,Void> {
        Context context;
        private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String comment ;
        String UID=String.valueOf(UUID.randomUUID());
        public GetTimeFromNet(Context context, String comment) {
            this.comment=comment;
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document documentHour= Jsoup.connect(baseUrlHour).get();
                Document documentDate= Jsoup.connect(baseUrlHour).get();
                Elements elementHour=documentHour.select("span[id=clk_hm]");
                Elements elementDate=documentDate.select("span[id=ij2]");
                date=elementDate.text() ;
                hour=elementHour.text();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("girdiHata",e.getMessage().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try{
                String [] splitDate=date.split(" ");
                int iDate=Integer.parseInt(splitDate[0]);
                int cDate=Integer.parseInt(getCurrentDate().substring(0,2));

                int iHour=Integer.parseInt(hour.substring(0,2));
                int iMinute=Integer.parseInt(hour.substring(3,5));

                int cHour=Integer.parseInt(getCurrentTime().substring(0,2));
                int cMinute=Integer.parseInt(getCurrentTime().substring(3,5));

                int tempMinute=Math.abs(cMinute-iMinute);
                if(iDate==cDate){
                    if(tempMinute<50){
                        CommentPost commentPost=new CommentPost();
                        commentPost.setDate(getCurrentDate());
                        commentPost.setHour(getCurrentTime());
                        commentPost.setComment(comment);
                        commentPost.setUID(UID);
                        commentPost.setName("Yetkili");
                        commentPost.setSurname("");
                        commentPost.setImeiNumber(imeiNumber);
                        databaseReference
                                .child(MenumConstant.STORE)
                                .child(phoneNumber)
                                .child(MenumConstant.PRODUCT_COMMENTS)
                                .child(menuCategoryName)
                                .child(productName)
                                .child(UID)
                                .setValue(commentPost);

                        context.startActivity(new Intent(context,ProductCommentDetail.class));
                        Toast.makeText(context,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Saat Ayalarınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                CommentPost commentPost=new CommentPost();
                commentPost.setDate(getCurrentDate());
                commentPost.setHour(getCurrentTime());
                commentPost.setComment(comment);
                commentPost.setUID(UID);
                commentPost.setName("Yetkili");
                commentPost.setSurname("");
                commentPost.setImeiNumber(imeiNumber);
                databaseReference
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PRODUCT_COMMENTS)
                        .child(menuCategoryName)
                        .child(productName)
                        .child(UID)
                        .setValue(commentPost);

                context.startActivity(new Intent(context,ProductCommentDetail.class));
                Toast.makeText(context,"Yorumunuz Gönderildi",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

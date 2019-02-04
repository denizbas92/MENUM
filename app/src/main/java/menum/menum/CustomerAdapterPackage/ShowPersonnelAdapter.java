package menum.menum.CustomerAdapterPackage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.Constant.MenumConstant;
import menum.menum.CustomerPackage.ShowPersonnel;
import menum.menum.Model.AddPersonnelPost;
import menum.menum.Model.NewNotificationPost;
import menum.menum.Model.NotificationCounterPost;
import menum.menum.Model.PersonnelsVotePost;
import menum.menum.R;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by deniz on 5.2.2018.
 */

public class ShowPersonnelAdapter extends RecyclerView.Adapter<ShowPersonnelAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber;
    private static boolean currentDate;
    private static String voteValue;
    private List<AddPersonnelPost> addPersonnelPostList;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private int dayofMonth[]= new int []{31,28,31,30,31,30,31,31,30,31,30,31};

    public ShowPersonnelAdapter(ShowPersonnel showPersonnel, String phoneNumber, List<AddPersonnelPost> addPersonnelPostList, boolean currentDate) {
        this.context=showPersonnel;
        this.phoneNumber=phoneNumber;
        this.addPersonnelPostList=addPersonnelPostList;
        this.currentDate=currentDate;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_show_personnel_adapter,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvName.setText(addPersonnelPostList.get(position).getName());
        holder.tvSurname.setText(addPersonnelPostList.get(position).getSurname());
        holder.tvResultRate.setText(" / "+addPersonnelPostList.get(position).getVoteRate());
        if(addPersonnelPostList.get(position).getImageUrl().equalsIgnoreCase("")==false){
            Glide.with(context).load(addPersonnelPostList.get(position).getImageUrl()).into(holder.imPersonnelIcon);
        }

        setVoteRate(holder,position);

        holder.ratePersonnel.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float vote, boolean b) {
                holder.tvResultRate.setText(Float.toString(vote));
                voteValue=Integer.toString(Math.round(vote));
            }
        });

        holder.btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentDate){
                    String cDay=getCurrentDate().substring(0,2);
                    String cMonth=getCurrentDate().substring(3,5);
                    String cYear=getCurrentDate().substring(6);
                    final String firstDate;
                    final String lastDate;
                    if(Integer.parseInt(cMonth)<10){
                        firstDate="01-"+cMonth+"-"+cYear;
                    }else{
                        firstDate="01-"+cMonth+"-"+cYear;
                    }
                    lastDate=Integer.toString(dayofMonth[Integer.parseInt(cMonth)-1])+"-"+cMonth+"-"+cYear;

                    databaseReference
                            .child(MenumConstant.CUSTOMERS)
                            .child(getImeiNumber())
                            .child(MenumConstant.PERSONNEL_VOTE)
                            .child(firstDate)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(addPersonnelPostList.get(position).getUID())){
                                        holder.ratePersonnel.setIsIndicator(true);
                                        StyleableToast st=new StyleableToast(context,"Sadece bir kere oy kullanabilirsiniz..", Toast.LENGTH_LONG);
                                        st.setBackgroundColor(Color.parseColor("#ff0000"));
                                        st.setTextColor(Color.WHITE);
                                        st.setCornerRadius(2);
                                        st.show();
                                    }else{
                                        holder.ratePersonnel.setIsIndicator(false);
                                        calculateVoteRate(position,firstDate,lastDate);

                                        NotificationCounterPost notificationCounterPost=new NotificationCounterPost();
                                        notificationCounterPost.setNewCounter("1");
                                        databaseReference
                                                .child(MenumConstant.STORE)
                                                .child(phoneNumber)
                                                .child(MenumConstant.NOTIFICATIONS)
                                                .child(MenumConstant.VOTE_NOTIFICATION)
                                                .child("newCounter")
                                                .setValue(notificationCounterPost.getNewCounter());

                                        NewNotificationPost newNotificationPost=new NewNotificationPost();
                                        newNotificationPost.setDate(getCurrentDate());
                                        newNotificationPost.setHour(getCurrentTime());
                                        newNotificationPost.setNotification((addPersonnelPostList.get(position).getName() + " " +
                                                addPersonnelPostList.get(position).getSurname() + " adlı personelinize yeni bir oy verildi."));
                                        newNotificationPost.setSeen("false");
                                        newNotificationPost.setProductName("");
                                        newNotificationPost.setMenuCategoryName("");
                                        newNotificationPost.setCodeNot("1");
                                        databaseReference
                                                .child(MenumConstant.STORE)
                                                .child(phoneNumber)
                                                .child(MenumConstant.NOTIFICATIONS)
                                                .child(MenumConstant.NEW_NOTIFICATION)
                                                .child(getCurrentDate())
                                                .child(getCurrentTime())
                                                .setValue(newNotificationPost);

                                        StyleableToast st=new StyleableToast(context,"Oyunuz Alınmıştır. Teşekkür ederiz.", Toast.LENGTH_LONG);
                                        st.setBackgroundColor(Color.parseColor("#0000ff"));
                                        st.setTextColor(Color.WHITE);
                                        st.setIcon(R.drawable.ic_tick);
                                        st.setCornerRadius(2);
                                        st.show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }else{
                    Toast.makeText(context,"Tarih Ayalarınızı Düzeltiniz",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateVoteRate(final int position, final String firstDate, final String lastDate) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNEL_VOTE)
                .child(firstDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(addPersonnelPostList.get(position).getUID())){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PERSONNEL_VOTE)
                                    .child(firstDate)
                                    .child(addPersonnelPostList.get(position).getUID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            PersonnelsVotePost personnelsVotePost=dataSnapshot.getValue(PersonnelsVotePost.class);
                                            int vote=Integer.parseInt(personnelsVotePost.getVoteCounter());
                                            vote=vote+1;
                                            int voteRate=Integer.parseInt(personnelsVotePost.getVoteRate())+Integer.parseInt(voteValue);
                                            int newVoteRate=Math.round((float) voteRate/(float) vote);
                                            personnelsVotePost.setVoteCounter(Integer.toString(vote));
                                            personnelsVotePost.setVoteRate(Integer.toString(newVoteRate));
                                            personnelsVotePost.setFirstDate(firstDate);
                                            personnelsVotePost.setLastDate(lastDate);

                                            databaseReference
                                                    .child(MenumConstant.STORE)
                                                    .child(phoneNumber)
                                                    .child(MenumConstant.PERSONNEL_VOTE)
                                                    .child(firstDate)
                                                    .child(addPersonnelPostList.get(position).getUID())
                                                    .setValue(personnelsVotePost);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }else{
                            PersonnelsVotePost personnelsVotePost=
                                    new PersonnelsVotePost(firstDate,lastDate,voteValue,"1");
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PERSONNEL_VOTE)
                                    .child(firstDate)
                                    .child(addPersonnelPostList.get(position).getUID())
                                    .setValue(personnelsVotePost);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        AddPersonnelPost addPersonnelPost=new AddPersonnelPost();
        addPersonnelPost.setVoteRate(voteValue);
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .child(getImeiNumber())
                .child(MenumConstant.PERSONNEL_VOTE)
                .child(firstDate)
                .child(addPersonnelPostList.get(position).getUID())
                .setValue(addPersonnelPost);
    }

    private void setVoteRate(final MyViewHolder holder, final int position) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(getImeiNumber())){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(getImeiNumber())
                                    .child(MenumConstant.PERSONNEL_VOTE)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String cDay=getCurrentDate().substring(0,2);
                                            String cMonth=getCurrentDate().substring(3,5);
                                            String cYear=getCurrentDate().substring(6);
                                            final String finalDate;
                                            if(Integer.parseInt(cMonth)<10){
                                                finalDate="01-"+cMonth+"-"+cYear;
                                            }else{
                                                finalDate="01-"+cMonth+"-"+cYear;
                                            }
                                            if(dataSnapshot.hasChild(finalDate)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(getImeiNumber())
                                                        .child(MenumConstant.PERSONNEL_VOTE)
                                                        .child(finalDate)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(addPersonnelPostList.get(position).getUID())){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(getImeiNumber())
                                                                            .child(MenumConstant.PERSONNEL_VOTE)
                                                                            .child(finalDate)
                                                                            .child(addPersonnelPostList.get(position).getUID())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                                                                    holder.ratePersonnel.setRating(Float.parseFloat(addPersonnelPost.getVoteRate()));
                                                                                    holder.ratePersonnel.setIsIndicator(true);
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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return addPersonnelPostList.size();
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

    public String getImeiNumber(){
        int permissionCheck= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            return "";
        }else{
            String deviceIMEI = telephonyManager.getDeviceId();
            return deviceIMEI;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imPersonnelIcon;
        private TextView tvName,tvSurname,tvResultRate;
        private RatingBar ratePersonnel;
        private Button btnVote;
        public MyViewHolder(View itemView) {
            super(itemView);
            imPersonnelIcon=itemView.findViewById(R.id.imPersonnelIcon);
            tvName=itemView.findViewById(R.id.tvName);
            tvSurname=itemView.findViewById(R.id.tvSurname);
            tvResultRate=itemView.findViewById(R.id.tvResultRate);
            ratePersonnel=itemView.findViewById(R.id.ratePersonnel);
            btnVote=itemView.findViewById(R.id.btnVote);
        }
    }
}

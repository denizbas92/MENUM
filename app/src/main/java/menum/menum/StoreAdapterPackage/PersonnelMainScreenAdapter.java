package menum.menum.StoreAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;
import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.Products;
import menum.menum.Model.AddPersonnelPost;
import menum.menum.Model.PersonnelsVotePost;
import menum.menum.PersonnelPackage.EditPersonnel;
import menum.menum.PersonnelPackage.PersonnelMainScreen;
import menum.menum.R;

/**
 * Created by deniz on 4.2.2018.
 */

public class PersonnelMainScreenAdapter extends RecyclerView.Adapter<PersonnelMainScreenAdapter.MyViewHolder>  {

    Context context;
    private static String phoneNumber;
    private List<AddPersonnelPost> addPersonnelPostList;
    private List<PersonnelsVotePost> personnelsVotePostList=new ArrayList<>();
    private PersonnelVoteDetailAdapter personnelVoteDetailAdapter;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public PersonnelMainScreenAdapter(PersonnelMainScreen personnelMainScreen, String phoneNumber, List<AddPersonnelPost> addPersonnelPostList) {
        this.context=personnelMainScreen;
        this.phoneNumber=phoneNumber;
        this.addPersonnelPostList=addPersonnelPostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_personnel,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvName.setText(addPersonnelPostList.get(position).getName());
        holder.tvSurname.setText(addPersonnelPostList.get(position).getSurname());
        if(addPersonnelPostList.get(position).getImageUrl().equalsIgnoreCase("")==false){
            Glide.with(context).load(addPersonnelPostList.get(position).getImageUrl()).into(holder.imPersonnelIcon);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditPersonnel(phoneNumber,addPersonnelPostList.get(position).getUID());
                context.startActivity(new Intent(context,EditPersonnel.class));
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePersonnel(position);
            }
        });

        holder.btnVoteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVoteDetail(position);
            }
        });
    }

    private void deletePersonnel(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
        TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
        Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
        Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
        tvTitle.setText(addPersonnelPostList.get(position).getName() + " "+addPersonnelPostList.get(position).getSurname() +
                " adlı personeliniz silinecek bunu onaylıyor musunuz ?");
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
                        .child(MenumConstant.STORE)
                        .child(phoneNumber)
                        .child(MenumConstant.PERSONNELS)
                        .child(addPersonnelPostList.get(position).getUID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                AddPersonnelPost addPersonnelPost=dataSnapshot.getValue(AddPersonnelPost.class);
                                if(addPersonnelPost.getImageUrl().equalsIgnoreCase("")==false){
                                    StorageReference storageReference =
                                            FirebaseStorage.getInstance().
                                                    getReferenceFromUrl(addPersonnelPostList.get(position).getImageUrl());
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.e("firebasestorage", "onFailure: did not delete file");
                                        }
                                    });
                                }

                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.PERSONNELS)
                                        .child(addPersonnelPostList.get(position).getUID())
                                        .removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(context,"Personel Silindi",Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context,PersonnelMainScreen.class));
                    }
                },1000);
            }
        });
    }

    private void showVoteDetail(final int position) {
        personnelsVotePostList.clear();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.layout_personnel_vote_detail_dialog,null);
        Button btnCancel=view.findViewById(R.id.btnCancel);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        final RecyclerView recVoteDetail=view.findViewById(R.id.recVoteDetail);
        recVoteDetail.hasFixedSize();
        recVoteDetail.setLayoutManager(linearLayoutManager);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.PERSONNEL_VOTE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapDate:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PERSONNEL_VOTE)
                                    .child(snapDate.getKey().toString())
                                    .child(addPersonnelPostList.get(position).getUID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            PersonnelsVotePost personnelsVotePost=dataSnapshot.getValue(PersonnelsVotePost.class);
                                            personnelsVotePostList.add(personnelsVotePost);
                                            personnelVoteDetailAdapter=new PersonnelVoteDetailAdapter(context,personnelsVotePostList);
                                            recVoteDetail.setAdapter(personnelVoteDetailAdapter);
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName,tvSurname;
        private Button btnDelete;
        private Button btnEdit;
        private Button btnVoteDetail;
        private CircleImageView imPersonnelIcon;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvSurname=itemView.findViewById(R.id.tvSurname);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnEdit=itemView.findViewById(R.id.btnEdit);
            btnVoteDetail=itemView.findViewById(R.id.btnVoteDetail);
            imPersonnelIcon=itemView.findViewById(R.id.imPersonnelIcon);
        }
    }
}

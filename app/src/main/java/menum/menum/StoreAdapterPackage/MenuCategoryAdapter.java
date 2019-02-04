package menum.menum.StoreAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.InterfacePackage.ClickEventListener;
import menum.menum.MainScreenPackage.MenuCategory;
import menum.menum.MainScreenPackage.Products;
import menum.menum.Model.AddProductPost;
import menum.menum.R;

/**
 * Created by deniz on 2.2.2018.
 */

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private List<String> listMenuCategory;

    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public MenuCategoryAdapter(MenuCategory menuCategory, String phoneNumber, List<String> listMenuCategory) {
        this.context=menuCategory;
        this.phoneNumber=phoneNumber;
        this.listMenuCategory=listMenuCategory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_category_name,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.btnMenuCategory.setText(listMenuCategory.get(position));
        holder.setClickEventListener(new ClickEventListener() {
            @Override
            public void OnClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    showEditMenuCategory(listMenuCategory.get(position));
                }else{
                    new Products(phoneNumber,listMenuCategory.get(position));
                    context.startActivity(new Intent(context,Products.class));
                }
            }
        });
    }

    private void showEditMenuCategory(final String menuCategoryName) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.layout_edit_menu_category_name,null);
        Button btnUpdate=view.findViewById(R.id.btnUpdate);
        Button btnDelete=view.findViewById(R.id.btnDelete);
        Button btnCancel=view.findViewById(R.id.btnCancel);

        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
                final TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
                Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
                Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
                tvTitle.setText(menuCategoryName + " adlı kategori ve altındaki ürünler kalıcı olarak silinecek.Bunu onaylıyor musunuz ?");
                builder.setView(viewConfirm);
                final AlertDialog dialogConfirm=builder.create();
                dialogConfirm.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                dialogConfirm.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogConfirm.dismiss();
                        dialog.dismiss();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteAllImage(menuCategoryName);
                        deleleteLikeDislikeCustomer(menuCategoryName);
                        deleteProductCommentCustomer(menuCategoryName);
                        deleteAllImage(menuCategoryName);
                        dialogConfirm.dismiss();
                        dialog.dismiss();
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateOption(menuCategoryName,dialog);

            }
        });
    }

    private void updateOption(final String menuCategoryName, final AlertDialog dialog) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View viewUpdate=LayoutInflater.from(context).inflate(R.layout.layout_update_menu_category_name,null);
        Button btnSave=viewUpdate.findViewById(R.id.btnSave);
        Button btnCancel=viewUpdate.findViewById(R.id.btnCancel);
        final EditText etMenuCategoryName=viewUpdate.findViewById(R.id.etMenuCategoryName);
        etMenuCategoryName.setText(menuCategoryName);

        builder.setView(viewUpdate);
        final AlertDialog dialogUpdate=builder.create();
        dialogUpdate.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialogUpdate.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdate.dismiss();
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newMenuCategoryName=etMenuCategoryName.getText().toString();

                if(TextUtils.isEmpty(newMenuCategoryName)){
                    Snackbar.make(view, "Kategori Adı Giriniz", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(newMenuCategoryName.equals(menuCategoryName)){
                    dialogUpdate.dismiss();
                }else{
                    updateProductCommentStore(menuCategoryName,newMenuCategoryName);
                    updateLikeDislikeStore(menuCategoryName,newMenuCategoryName);
                    updateMenuCategoryStore(menuCategoryName,newMenuCategoryName);
                    updateLikeDislikeCustomer(menuCategoryName,newMenuCategoryName);
                    updateProductCommentCustomer(menuCategoryName,newMenuCategoryName);
                }

                dialogUpdate.dismiss();
                dialog.dismiss();
                StyleableToast st=new StyleableToast(context,"Güncelleme Tamamlandı.", Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#0000ff"));
                st.setTextColor(Color.WHITE);
                st.setCornerRadius(2);
                st.show();
            }
        });
    }

    private void deleteAllImage(final String menuCategoryName) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MENU_CATEGORY)
                .child(menuCategoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapProductName : dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_CATEGORY)
                                    .child(menuCategoryName)
                                    .child(snapProductName.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            AddProductPost addProductPost=dataSnapshot.getValue(AddProductPost.class);
                                            if(addProductPost.getProductImageUrl().equalsIgnoreCase("")==false){
                                                StorageReference storageReference =
                                                        FirebaseStorage.getInstance().
                                                                getReferenceFromUrl(addProductPost.getProductImageUrl());
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // File deleted successfully
                                                  //      Toast.makeText(context,"Ürün Resmi Silindi",Toast.LENGTH_SHORT).show();
                                                        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY).child(menuCategoryName).removeValue();
                                                        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_LIKE).child(menuCategoryName).removeValue();
                                                        databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.PRODUCT_COMMENTS).child(menuCategoryName).removeValue();
                                                        StyleableToast st=new StyleableToast(context,"Kategori Silindi.", Toast.LENGTH_LONG);
                                                        st.setBackgroundColor(Color.parseColor("#0000ff"));
                                                        st.setTextColor(Color.WHITE);
                                                        st.setCornerRadius(2);
                                                        st.show();
                                                        context.startActivity(new Intent(context,MenuCategory.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Uh-oh, an error occurred!
                                                        Log.e("firebasestorage", "onFailure: did not delete file");
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

    private void updateMenuCategoryStore(String menuCategoryName, String newMenuCategoryName) {
        final DatabaseReference dbFrom=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY).child(menuCategoryName);
        final DatabaseReference dbTo=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_CATEGORY).child(newMenuCategoryName);

        dbFrom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbTo.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dbFrom.removeValue();
                                    context.startActivity(new Intent(context,MenuCategory.class));
                                }
                            },1000);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateLikeDislikeStore(String menuCategoryName, String newMenuCategoryName) {
        final DatabaseReference dbFromLike=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_LIKE).child(menuCategoryName);
        final DatabaseReference dbToLike=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.MENU_LIKE).child(newMenuCategoryName);

        dbFromLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbToLike.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dbFromLike.removeValue();
                                }
                            },1000);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProductCommentStore(String menuCategoryName, String newMenuCategoryName) {
        final DatabaseReference dbFromProductComment=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.PRODUCT_COMMENTS).child(menuCategoryName);
        final DatabaseReference dbToProductComment=databaseReference.child(MenumConstant.STORE).child(phoneNumber).child(MenumConstant.PRODUCT_COMMENTS).child(newMenuCategoryName);

        dbFromProductComment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbToProductComment.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dbFromProductComment.removeValue();
                                }
                            },1000);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProductCommentCustomer(final String menuCategoryName, final String newMenuCategoryName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.PRODUCT_COMMENTS)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.PRODUCT_COMMENTS)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        final DatabaseReference dbFrom=databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName);
                                                                                        final DatabaseReference dbTo=databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                .child(phoneNumber)
                                                                                                .child(newMenuCategoryName);
                                                                                        dbFrom.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                dbTo.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                                                                    @Override
                                                                                                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                                                                                                        if (databaseError != null) {
                                                                                                            System.out.println("Copy failed");
                                                                                                        } else {
                                                                                                            Handler handler= new Handler();
                                                                                                            handler.postDelayed(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    dbFrom.removeValue();
                                                                                                                }
                                                                                                            },1000);
                                                                                                        }
                                                                                                    }
                                                                                                });
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateLikeDislikeCustomer(final String menuCategoryName, final String newMenuCategoryName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.LIKE_DISLIKE)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.LIKE_DISLIKE)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        final DatabaseReference dbFrom=databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.LIKE_DISLIKE)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName);
                                                                                        final DatabaseReference dbTo=databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.LIKE_DISLIKE)
                                                                                                .child(phoneNumber)
                                                                                                .child(newMenuCategoryName);
                                                                                        dbFrom.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                dbTo.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                                                                    @Override
                                                                                                    public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                                                                                                        if (databaseError != null) {
                                                                                                            System.out.println("Copy failed");
                                                                                                        } else {
                                                                                                            Handler handler= new Handler();
                                                                                                            handler.postDelayed(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    dbFrom.removeValue();
                                                                                                                }
                                                                                                            },1000);
                                                                                                        }
                                                                                                    }
                                                                                                });
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void deleteProductCommentCustomer(final String menuCategoryName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.PRODUCT_COMMENTS)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.PRODUCT_COMMENTS)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName)
                                                                                                .removeValue();
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void deleleteLikeDislikeCustomer(final String menuCategoryName) {
        databaseReference
                .child(MenumConstant.CUSTOMERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snapImai:dataSnapshot.getChildren()){
                            databaseReference
                                    .child(MenumConstant.CUSTOMERS)
                                    .child(snapImai.getKey().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(MenumConstant.LIKE_DISLIKE)){
                                                databaseReference
                                                        .child(MenumConstant.CUSTOMERS)
                                                        .child(snapImai.getKey().toString())
                                                        .child(MenumConstant.LIKE_DISLIKE)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(phoneNumber)){
                                                                    databaseReference
                                                                            .child(MenumConstant.CUSTOMERS)
                                                                            .child(snapImai.getKey().toString())
                                                                            .child(MenumConstant.LIKE_DISLIKE)
                                                                            .child(phoneNumber)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.hasChild(menuCategoryName)){
                                                                                        databaseReference
                                                                                                .child(MenumConstant.CUSTOMERS)
                                                                                                .child(snapImai.getKey().toString())
                                                                                                .child(MenumConstant.LIKE_DISLIKE)
                                                                                                .child(phoneNumber)
                                                                                                .child(menuCategoryName)
                                                                                                .removeValue();
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return listMenuCategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private Button btnMenuCategory;
        private ClickEventListener clickEventListener;
        public MyViewHolder(View itemView) {
            super(itemView);
            btnMenuCategory=itemView.findViewById(R.id.btnMenuCategory);
            btnMenuCategory.setOnClickListener(this);
            btnMenuCategory.setOnLongClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void setClickEventListener(ClickEventListener clickEventListener) {
            this.clickEventListener = clickEventListener;
        }

        @Override
        public void onClick(View v) {
            clickEventListener.OnClick(v,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickEventListener.OnClick(view,getAdapterPosition(),true);
            return true;
        }
    }
}

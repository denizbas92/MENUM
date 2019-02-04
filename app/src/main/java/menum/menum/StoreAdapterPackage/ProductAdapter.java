package menum.menum.StoreAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import menum.menum.Constant.MenumConstant;
import menum.menum.MainScreenPackage.EditProduct;
import menum.menum.MainScreenPackage.EditProductImage;
import menum.menum.MainScreenPackage.ProductDetail;
import menum.menum.MainScreenPackage.Products;
import menum.menum.Model.AddProductPost;
import menum.menum.R;

/**
 * Created by deniz on 3.2.2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private static String menuCategoryName;
    private List<AddProductPost> addProductPostList;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public ProductAdapter(Products products, String phoneNumber, String menuCategoryName, List<AddProductPost> addProductPostList) {
        this.context=products;
        this.addProductPostList=addProductPostList;
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try{
            holder.tvProductName.setText(addProductPostList.get(position).getProductName());
            holder.tvProductPrice.setText(addProductPostList.get(position).getProductPrice());
            if(addProductPostList.get(position).getProductImageUrl().equalsIgnoreCase("")==false){
                ViewTarget viewTarget=new ViewTarget(holder.relative);
                Glide.with(context).load(addProductPostList.get(position).getProductImageUrl()).fitCenter().centerCrop().into(viewTarget);
            }
            if(addProductPostList.get(position).getSalesSituation().equalsIgnoreCase("false")){
                holder.tvOver.setVisibility(View.VISIBLE);
            }else{
                holder.tvOver.setVisibility(View.INVISIBLE);
            }

            holder.btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_confirm_dialog,null);
                    TextView tvTitle=viewConfirm.findViewById(R.id.tvTitle);
                    Button btnCancel=viewConfirm.findViewById(R.id.btnCancel);
                    Button btnConfirm=viewConfirm.findViewById(R.id.btnConfirm);
                    tvTitle.setText(addProductPostList.get(position).getProductName() + " adlı ürününüz silinecek bunu onaylıyor musunuz ?");
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
                            if(addProductPostList.get(position).getProductImageUrl().equalsIgnoreCase("")){
                                databaseReference
                                        .child(MenumConstant.STORE)
                                        .child(phoneNumber)
                                        .child(MenumConstant.MENU_CATEGORY)
                                        .child(menuCategoryName)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getChildrenCount()==1){
                                                    databaseReference
                                                            .child(MenumConstant.STORE)
                                                            .child(phoneNumber)
                                                            .child(MenumConstant.MENU_CATEGORY)
                                                            .child(menuCategoryName)
                                                            .child(menuCategoryName)
                                                            .setValue(menuCategoryName);
                                                    databaseReference
                                                            .child(MenumConstant.STORE)
                                                            .child(phoneNumber)
                                                            .child(MenumConstant.MENU_CATEGORY)
                                                            .child(menuCategoryName)
                                                            .child(addProductPostList.get(position).getProductName())
                                                            .removeValue();
                                                    deleteFromPersonbelMenuLike(addProductPostList.get(position).getProductName());
                                                    deleteFromPersonbelProductComment(addProductPostList.get(position).getProductName());
                                                    deleteFromCustomerProductComments(addProductPostList.get(position).getProductName());
                                                    deleteFromCustomerLikeDislike(addProductPostList.get(position).getProductName());
                                                }else{
                                                    databaseReference
                                                            .child(MenumConstant.STORE)
                                                            .child(phoneNumber)
                                                            .child(MenumConstant.MENU_CATEGORY)
                                                            .child(menuCategoryName)
                                                            .child(addProductPostList.get(position).getProductName())
                                                            .removeValue();
                                                    deleteFromPersonbelMenuLike(addProductPostList.get(position).getProductName());
                                                    deleteFromPersonbelProductComment(addProductPostList.get(position).getProductName());
                                                    deleteFromCustomerProductComments(addProductPostList.get(position).getProductName());
                                                    deleteFromCustomerLikeDislike(addProductPostList.get(position).getProductName());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }else{
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance().
                                                getReferenceFromUrl(addProductPostList.get(position).getProductImageUrl());
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Toast.makeText(context,"Ürün Resmi Silindi",Toast.LENGTH_SHORT).show();
                                        databaseReference
                                                .child(MenumConstant.STORE)
                                                .child(phoneNumber)
                                                .child(MenumConstant.MENU_CATEGORY)
                                                .child(menuCategoryName)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.getChildrenCount()==1){
                                                            databaseReference
                                                                    .child(MenumConstant.STORE)
                                                                    .child(phoneNumber)
                                                                    .child(MenumConstant.MENU_CATEGORY)
                                                                    .child(menuCategoryName)
                                                                    .child(menuCategoryName)
                                                                    .setValue(menuCategoryName);
                                                            databaseReference
                                                                    .child(MenumConstant.STORE)
                                                                    .child(phoneNumber)
                                                                    .child(MenumConstant.MENU_CATEGORY)
                                                                    .child(menuCategoryName)
                                                                    .child(addProductPostList.get(position).getProductName())
                                                                    .removeValue();
                                                            deleteFromPersonbelMenuLike(addProductPostList.get(position).getProductName());
                                                            deleteFromPersonbelProductComment(addProductPostList.get(position).getProductName());
                                                            deleteFromCustomerProductComments(addProductPostList.get(position).getProductName());
                                                            deleteFromCustomerLikeDislike(addProductPostList.get(position).getProductName());
                                                        }else{
                                                            databaseReference
                                                                    .child(MenumConstant.STORE)
                                                                    .child(phoneNumber)
                                                                    .child(MenumConstant.MENU_CATEGORY)
                                                                    .child(menuCategoryName)
                                                                    .child(addProductPostList.get(position).getProductName())
                                                                    .removeValue();
                                                            deleteFromPersonbelMenuLike(addProductPostList.get(position).getProductName());
                                                            deleteFromPersonbelProductComment(addProductPostList.get(position).getProductName());
                                                            deleteFromCustomerProductComments(addProductPostList.get(position).getProductName());
                                                            deleteFromCustomerLikeDislike(addProductPostList.get(position).getProductName());

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        Log.e("firebasestorage", "onFailure: did not delete file");
                                    }
                                });
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"Ürün Silindi",Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context,Products.class));
                                }
                            },1500);
                        }
                    });
                }
            });

            holder.btnEditProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View viewConfirm=LayoutInflater.from(context).inflate(R.layout.layout_edit_product_option,null);
                    Button btnProductSettings=viewConfirm.findViewById(R.id.btnProductSettings);
                    Button btnImageSettings=viewConfirm.findViewById(R.id.btnImageSettings);
                    builder.setView(viewConfirm);
                    final AlertDialog dialog=builder.create();
                    dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                    dialog.show();

                    btnProductSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new EditProduct(phoneNumber,menuCategoryName,addProductPostList.get(position).getProductName());
                            context.startActivity(new Intent(context,EditProduct.class));
                        }
                    });

                    btnImageSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new EditProductImage(phoneNumber,menuCategoryName,addProductPostList.get(position).getProductName());
                            context.startActivity(new Intent(context,EditProductImage.class));
                        }
                    });
                }
            });

            holder.cardProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ProductDetail(phoneNumber,menuCategoryName,addProductPostList.get(position).getProductName());
                    context.startActivity(new Intent(context,ProductDetail.class));
                }
            });
        }catch (Exception e){

        }
    }

    private void deleteFromPersonbelMenuLike(final String productName) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.MENU_LIKE)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.MENU_LIKE)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(menuCategoryName)){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.MENU_LIKE)
                                                        .child(menuCategoryName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(productName)){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.MENU_LIKE)
                                                                            .child(menuCategoryName)
                                                                            .child(productName)
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

    private void deleteFromPersonbelProductComment(final String productName) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(MenumConstant.PRODUCT_COMMENTS)){
                            databaseReference
                                    .child(MenumConstant.STORE)
                                    .child(phoneNumber)
                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(menuCategoryName)){
                                                databaseReference
                                                        .child(MenumConstant.STORE)
                                                        .child(phoneNumber)
                                                        .child(MenumConstant.PRODUCT_COMMENTS)
                                                        .child(menuCategoryName)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(dataSnapshot.hasChild(productName)){
                                                                    databaseReference
                                                                            .child(MenumConstant.STORE)
                                                                            .child(phoneNumber)
                                                                            .child(MenumConstant.PRODUCT_COMMENTS)
                                                                            .child(menuCategoryName)
                                                                            .child(productName)
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

    private void deleteFromCustomerLikeDislike(final String productName) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MYCUSTOMERS)
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
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        if(dataSnapshot.hasChild(productName)){
                                                                                                            databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.LIKE_DISLIKE)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(productName)
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void deleteFromCustomerProductComments(final String productName) {
        databaseReference
                .child(MenumConstant.STORE)
                .child(phoneNumber)
                .child(MenumConstant.MYCUSTOMERS)
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
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                        if(dataSnapshot.hasChild(productName)){
                                                                                                            databaseReference
                                                                                                                    .child(MenumConstant.CUSTOMERS)
                                                                                                                    .child(snapImai.getKey().toString())
                                                                                                                    .child(MenumConstant.PRODUCT_COMMENTS)
                                                                                                                    .child(phoneNumber)
                                                                                                                    .child(menuCategoryName)
                                                                                                                    .child(productName)
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return addProductPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relative;
        private CardView cardProduct;
        private TextView tvProductName,tvProductPrice,tvOver;
        private Button btnDeleteProduct,btnEditProduct;
        public MyViewHolder(View itemView) {
            super(itemView);
            relative=itemView.findViewById(R.id.relative);
            cardProduct=itemView.findViewById(R.id.cardProduct);
            tvProductName=itemView.findViewById(R.id.tvProductName);
            tvProductPrice=itemView.findViewById(R.id.tvProductPrice);
            tvOver=itemView.findViewById(R.id.tvOver);
            btnDeleteProduct=itemView.findViewById(R.id.btnDeleteProduct);
            btnEditProduct=itemView.findViewById(R.id.btnEditProduct);
        }
    }

    public class ViewTarget extends ViewTarget2<GlideDrawable> {

        public ViewTarget(ViewGroup view) {
            super(view);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setBackground(resource);
        }
    }
    public abstract class ViewTarget2<Z> extends com.bumptech.glide.request.target.ViewTarget<ViewGroup,Z> implements GlideAnimation.ViewAdapter {


        public ViewTarget2(ViewGroup view) {
            super(view);
        }

        @Override
        public Drawable getCurrentDrawable() {
            return view.getBackground();
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param drawable {@inheritDoc}
         */
        @Override
        public void setDrawable(Drawable drawable) {
            view.setBackground(drawable);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param placeholder {@inheritDoc}
         */
        @Override
        public void onLoadStarted(Drawable placeholder) {
            view.setBackground(placeholder);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param errorDrawable {@inheritDoc}
         */
        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            view.setBackground(errorDrawable);
        }

        /**
         * Sets the given {@link android.graphics.drawable.Drawable} on the view using
         * {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
         *
         * @param placeholder {@inheritDoc}
         */
        @Override
        public void onLoadCleared(Drawable placeholder) {
            view.setBackground(placeholder);
        }

        @Override
        public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {

            this.setResource(resource);
        }

        protected abstract void setResource(Z resource);

    }
}

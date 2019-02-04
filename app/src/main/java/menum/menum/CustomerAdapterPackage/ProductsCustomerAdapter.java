package menum.menum.CustomerAdapterPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.CustomerPackage.ProductsCustomer;
import menum.menum.Model.AddProductPost;
import menum.menum.R;
import menum.menum.StoreAdapterPackage.ProductAdapter;

/**
 * Created by deniz on 5.2.2018.
 */

public class ProductsCustomerAdapter extends RecyclerView.Adapter<ProductsCustomerAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private static String menuCategoryName;
    private List<AddProductPost> addProductPostList;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

    public ProductsCustomerAdapter(ProductsCustomer productsCustomer, String phoneNumber, String menuCategoryName, List<AddProductPost> addProductPostList) {
        this.context=productsCustomer;
        this.addProductPostList=addProductPostList;
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_customer,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
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

        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProductDetailCustomer(phoneNumber,menuCategoryName,addProductPostList.get(position).getProductName());
                context.startActivity(new Intent(context,ProductDetailCustomer.class));
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

        public MyViewHolder(View itemView) {
            super(itemView);
            relative=itemView.findViewById(R.id.relative);
            cardProduct=itemView.findViewById(R.id.cardProduct);
            tvProductName=itemView.findViewById(R.id.tvProductName);
            tvProductPrice=itemView.findViewById(R.id.tvProductPrice);
            tvOver=itemView.findViewById(R.id.tvOver);
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

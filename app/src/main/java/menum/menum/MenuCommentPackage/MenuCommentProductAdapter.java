package menum.menum.MenuCommentPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.List;

import menum.menum.CustomerAdapterPackage.ProductsCustomerAdapter;
import menum.menum.CustomerPackage.ProductDetailCustomer;
import menum.menum.Model.LikeDislikePost;
import menum.menum.R;

/**
 * Created by deniz on 9.2.2018.
 */

public class MenuCommentProductAdapter extends RecyclerView.Adapter<MenuCommentProductAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private static String menuCategoryName ;
    private List<LikeDislikePost> likeDislikePostList;
    private List<String> listProductName;

    public MenuCommentProductAdapter(MenuCommentProducts menuCommentProducts, String phoneNumber, String menuCategoryName, List<String> listProductName,List<LikeDislikePost> likeDislikePostList) {
        this.context=menuCommentProducts;
        this.phoneNumber=phoneNumber;
        this.menuCategoryName=menuCategoryName;
        this.listProductName=listProductName;
        this.likeDislikePostList=likeDislikePostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_comment_product,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvLikeCounter.setText(likeDislikePostList.get(position).getLikeCounter());
        holder.tvUnLikeCounter.setText(likeDislikePostList.get(position).getDislikeCounter());
        holder.tvProductName.setText(listProductName.get(position));
        if(likeDislikePostList.get(position).getProductImageUrl().equalsIgnoreCase("")==false){
            ViewTarget viewTarget=new ViewTarget(holder.relative);
            Glide.with(context).load(likeDislikePostList.get(position).getProductImageUrl()).fitCenter().centerCrop().into(viewTarget);
        }
        if(likeDislikePostList.get(position).getSalesSituation().equalsIgnoreCase("false")){
            holder.tvOver.setVisibility(View.VISIBLE);
        }else{
            holder.tvOver.setVisibility(View.INVISIBLE);
        }

        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProductCommentDetail(phoneNumber,menuCategoryName,listProductName.get(position));
                context.startActivity(new Intent(context,ProductCommentDetail.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return likeDislikePostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvOver,tvLikeCounter,tvUnLikeCounter,tvProductName;
        CardView cardProduct;
        RelativeLayout relative;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvOver=itemView.findViewById(R.id.tvOver);
            tvLikeCounter=itemView.findViewById(R.id.tvLikeCounter);
            tvUnLikeCounter=itemView.findViewById(R.id.tvUnLikeCounter);
            cardProduct=itemView.findViewById(R.id.cardProduct);
            relative=itemView.findViewById(R.id.relative);
            tvProductName=itemView.findViewById(R.id.tvProductName);
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

package menum.menum.CustomerAdapterPackage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import menum.menum.CustomerPackage.MenuCategoryCustomer;
import menum.menum.CustomerPackage.ProductsCustomer;
import menum.menum.MainScreenPackage.MenuCategory;
import menum.menum.MenuCommentPackage.MenuCommentCategory;
import menum.menum.MenuCommentPackage.MenuCommentProducts;
import menum.menum.R;

/**
 * Created by deniz on 5.2.2018.
 */

public class MenuCategoryCustomerAdapter extends RecyclerView.Adapter<MenuCategoryCustomerAdapter.MyViewHolder> {

    Context context;
    private static String phoneNumber ;
    private List<String> listMenuCategory;

    public MenuCategoryCustomerAdapter(MenuCategoryCustomer menuCategoryCustomer, String phoneNumber, List<String> listMenuCategory) {
        this.context=menuCategoryCustomer;
        this.phoneNumber=phoneNumber;
        this.listMenuCategory=listMenuCategory;
    }

    public MenuCategoryCustomerAdapter(MenuCommentCategory menuCommentCategory, String phoneNumber, List<String> listMenuCategory) {
        this.context=menuCommentCategory;
        this.phoneNumber=phoneNumber;
        this.listMenuCategory=listMenuCategory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_category_name,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.btnMenuCategory.setText(listMenuCategory.get(position));
        holder.btnMenuCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof MenuCommentCategory){
                    new MenuCommentProducts(phoneNumber,listMenuCategory.get(position));
                    context.startActivity(new Intent(context,MenuCommentProducts.class));
                }else{
                    new ProductsCustomer(phoneNumber,listMenuCategory.get(position));
                    context.startActivity(new Intent(context, ProductsCustomer.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMenuCategory.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Button btnMenuCategory;
        public MyViewHolder(View itemView) {
            super(itemView);
            btnMenuCategory=itemView.findViewById(R.id.btnMenuCategory);
        }
    }
}

package menum.menum.StoreAdapterPackage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import menum.menum.Model.PersonnelsVotePost;
import menum.menum.R;

/**
 * Created by deniz on 7.2.2018.
 */

public class PersonnelVoteDetailAdapter extends RecyclerView.Adapter<PersonnelVoteDetailAdapter.MyViewHolder> {

    Context context;
    private List<PersonnelsVotePost> personnelsVotePostList;

    public PersonnelVoteDetailAdapter(Context context, List<PersonnelsVotePost> personnelsVotePostList) {
        this.context=context;
        this.personnelsVotePostList=personnelsVotePostList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_personnel_vote_detail,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try{
            holder.tvFirstLastDate.setText(personnelsVotePostList.get(position).getFirstDate()+" / " + personnelsVotePostList.get(position).getLastDate());
            holder.tvResultRate.setText(" / " + personnelsVotePostList.get(position).getVoteRate());
            holder.tvVoteCounter.setText(personnelsVotePostList.get(position).getVoteCounter());
            holder.ratePersonnel.setRating(Float.parseFloat(personnelsVotePostList.get(position).getVoteRate()));
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return personnelsVotePostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvFirstLastDate,tvResultRate,tvVoteCounter;
        private RatingBar ratePersonnel;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvFirstLastDate=itemView.findViewById(R.id.tvFirstLastDate);
            tvResultRate=itemView.findViewById(R.id.tvResultRate);
            tvVoteCounter=itemView.findViewById(R.id.tvVoteCounter);
            ratePersonnel=itemView.findViewById(R.id.ratePersonnel);
        }
    }
}

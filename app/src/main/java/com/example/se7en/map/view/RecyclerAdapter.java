package com.example.se7en.map.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.se7en.map.R;
import com.example.se7en.map.model.Place;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<Place> mData;
    private int selectIndex = 0;

    public RecyclerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_main, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = mData.get(position);
        holder.name.setText(place.name);
        holder.address.setText(place.address);
        if (position == selectIndex){
            holder.selector.setVisibility(View.VISIBLE);
        }else {
            holder.selector.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView address;
        ImageView selector;
        RelativeLayout rlPlace;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            address = (TextView) view.findViewById(R.id.tv_address);
            selector = (ImageView) view.findViewById(R.id.iv_item_select);
            rlPlace = (RelativeLayout) view.findViewById(R.id.rl_place);
            rlPlace.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != selectIndex){
                notifyItemChanged(selectIndex);
                selectIndex = position;
                notifyItemChanged(selectIndex);
                if (clickListener != null) {
                    clickListener.onPlaceClick(itemView, getAdapterPosition());
                }
            }
        }
    }

    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onPlaceClick(View view, int position);
    }

    public void setData(List<Place> mDatas) {
        this.mData = mDatas;
    }

    public Place get(int aPosition){
        return mData.get(aPosition);
    }
}
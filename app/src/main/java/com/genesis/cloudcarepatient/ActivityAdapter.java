package com.genesis.cloudcarepatient;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private OnItemClickWatcher watcher;
    private int[] dataArray;
    private Context context;
    private TypedArray imgArray;

    public ActivityAdapter(int[] objects, Context cont, OnItemClickWatcher watcher) {
        this.dataArray = objects;
        this.context = cont;
        this.watcher = watcher;
        this.imgArray = context.getResources().obtainTypedArray(R.array.activity_icons_array);
    }
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, watcher, dataArray);
    }

    @Override
    public void onBindViewHolder(ActivityAdapter.ViewHolder holder, int position) {

        holder.imageView.setImageResource(imgArray.getResourceId(position, R.drawable.ic_patient));
        holder.activityValue.setText(String.valueOf(dataArray[position]) + "%");
        holder.activityType.setText(Utils.getStringActivity(position));
    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView activityType;
        public TextView activityValue;
        public ImageView imageView;


        public ViewHolder(View itemView, final OnItemClickWatcher watcher, final int[] resultArray) {
            super(itemView);
            rootView = itemView;
            imageView = (ImageView) rootView.findViewById(R.id.imageView);
            activityType = (TextView) rootView.findViewById(R.id.activity_type);
            activityValue = (TextView) rootView.findViewById(R.id.activity_value);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watcher.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}


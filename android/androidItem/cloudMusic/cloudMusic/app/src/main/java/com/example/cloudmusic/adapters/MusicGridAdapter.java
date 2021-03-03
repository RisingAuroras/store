package com.example.cloudmusic.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activitys.AlbumListActivity;
import com.example.cloudmusic.models.AlbumModel;

import java.util.List;

public class MusicGridAdapter extends RecyclerView.Adapter<MusicGridAdapter.ViewHolder> {
    private Context mContext;
    private List<AlbumModel> mDataSource;

    public MusicGridAdapter(Context context,List<AlbumModel> dataSource){
        mContext = context;
        this.mDataSource = dataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_grid_music,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AlbumModel albumModel = mDataSource.get(position);
        Glide.with(mContext)
                .load(albumModel.getPoster())
                .into(holder.ivIcon);
        holder.mTvPlayNum.setText(albumModel.getPlayNum());
        holder.mTvName.setText(albumModel.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumListActivity.class);
                intent.putExtra(AlbumListActivity.ALBUM_ID,albumModel.getAlbumId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivIcon;
        View itemView;
        TextView mTvPlayNum,mTvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivIcon = itemView.findViewById(R.id.iv_icon);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvPlayNum = itemView.findViewById(R.id.tv_play_num);

        }
    }
}

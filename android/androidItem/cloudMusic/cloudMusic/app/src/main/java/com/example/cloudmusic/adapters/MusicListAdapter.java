package com.example.cloudmusic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activitys.PlayMusicActivity;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.MusicSourceModel;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.ToastUtil;

import java.io.Serializable;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private RecyclerView mRv;
    private boolean isCalcaulationRvHeight;
    private List<MusicModel> mDataSource;

    public MusicListAdapter(Context mContext,RecyclerView recyclerView,List<MusicModel> dataSource) {
        this.mContext = mContext;
        this.mRv = recyclerView;
        this.mDataSource = dataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mItemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_list_music,viewGroup,false);
        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {//?
        setRecyclerViewHeight();

        final MusicModel musicModel = mDataSource.get(position);
        Glide.with(mContext)
                .load(musicModel.getPoster())
                .into(holder.ivIcon);

        holder.mTvName.setText(musicModel.getName());
        holder.mTvAuthor.setText(musicModel.getAuthor());

        holder.ivMusicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (position == 5) {

                        } else {
                            new AlertDialog.Builder(mContext).setTitle("确定删除歌曲吗").
                                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

//                                            PlaylistInfo.getInstance(mContext).deletePlaylist(playlistid);
//                                            PlaylistsManager.getInstance(mContext).delete(playlistid);
                                            Intent intent = new Intent();
//                                            intent.setAction(IConstants.PLAYLIST_COUNT_CHANGED);
                                            mContext.sendBroadcast(intent);
                                            dialog.dismiss();
                                        }
                                    }).
                                    setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                        return true;
                    }
                });
                popupMenu.inflate(R.menu.popmenu);
                popupMenu.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.ivPlay.setImageResource(R.mipmap.play_now);
                Intent intent = new Intent(mContext, PlayMusicActivity.class);
                intent.putExtra("position", position);
                intent.putExtra(PlayMusicActivity.MUSIC_ID,musicModel.getMusicId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    /**
     * 1、获取ItemView的高度
     * 2、itemView的数量
     * 3、使用 itemViewHeight * itemViewNum = RecyclerView的高度
     */
    public void setRecyclerViewHeight () {
        if (isCalcaulationRvHeight || mRv == null) return ;//高度只需要设置一次

        isCalcaulationRvHeight = true;
        //获取ItemView的高度
        RecyclerView.LayoutParams itemViewLp = (RecyclerView.LayoutParams) mItemView.getLayoutParams();
        //itemView的数量
        int itemCount = getItemCount();
        //使用itemViewHeight * itemViewNum = RecyclerView的高度
        int recyclerViewHeight = itemViewLp.height * itemCount;
        //设置RecyclerView高度
        LinearLayout.LayoutParams rvLp = (LinearLayout.LayoutParams) mRv.getLayoutParams();
        rvLp.height = recyclerViewHeight;
        mRv.setLayoutParams(rvLp);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView ivIcon,ivMusicMenu;
        TextView mTvName,mTvAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivIcon = itemView.findViewById(R.id.iv_icon);
            ivMusicMenu = itemView.findViewById(R.id.iv_music_menu);
            mTvAuthor = itemView.findViewById(R.id.tv_author);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }
}

package com.example.cloudmusic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.media.Image;
import android.text.TextUtils;
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
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.MusicSourceModel;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

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
//        if(!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅

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

        if(!TextUtils.isEmpty(musicModel.getPoster()))
            Glide.with(mContext)
                    .load(musicModel.getPoster())
                    .into(holder.ivIcon);
        else
            Glide.with(mContext)
                    .load("https://image.lnstzy.cn/aoaodcom/2019-08/17/201908170750199553.jpg.h700.jpg")
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
        if(musicModel.isCheck()) {
            holder.mTvName.setTextColor(Color.rgb(35,235,185));
            holder.mTvAuthor.setTextColor(Color.rgb(35,235,185));
        }
        else {
            holder.mTvName.setTextColor(Color.parseColor("#333333"));
            holder.mTvAuthor.setTextColor(Color.parseColor("#888888"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.ivPlay.setImageResource(R.mipmap.play_now);
                EventBus.getDefault().post(MessageWrap.getInstance(position,"musicList",mDataSource));//EventBus发布消息,用来展示播放状态
                EventBus.getDefault().post(MessageWrap.getInstance(position,"changeMusic",null));//EventBus发布消息,用来展示播放状态
//                EventBus.getDefault().post(MessageWrap.getInstance(-1,"PlayMusic",null));//EventBus发布消息,用来展示播放状态

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
    /**
     * 刷新数据
     */
    public void changeState() {
        notifyDataSetChanged();
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

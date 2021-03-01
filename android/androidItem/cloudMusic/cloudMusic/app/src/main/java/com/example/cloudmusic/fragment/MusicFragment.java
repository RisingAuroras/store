package com.example.cloudmusic.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapters.MusicGridAdapter;
import com.example.cloudmusic.adapters.MusicListAdapter;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.MusicSourceModel;
import com.example.cloudmusic.test.A;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.views.GridSpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MusicFragment extends Fragment {

    private View mContentView;
    private Unbinder unbinder;
    private Activity mContext;

    private RecyclerView mRvGrid,mRvList;
    private MusicGridAdapter mGridAdapter;
    private MusicListAdapter mListAdapter;
    private RealmHelper mRealmHelper;
    private MusicSourceModel mMusicSourceModel;

    //按两下退出app
    private static final int TIME_EXIT=2000;
    private long mBackPressed = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_music, container, false);

        EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅
        initView();
        initData();
        return mContentView;
    }

    private void initView() {
        unbinder = ButterKnife.bind(this, mContentView);
        mRealmHelper = new RealmHelper();
        mMusicSourceModel = mRealmHelper.getMusicSource();
    }

    private void initData() {
        mRvGrid = mContentView.findViewById(R.id.rv_grid);
        mRvGrid.setLayoutManager(new GridLayoutManager(mContext,3));
        mRvGrid.addItemDecoration(new GridSpaceItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.albumMarginSize),mRvGrid));
        mGridAdapter = new MusicGridAdapter(mContext,mMusicSourceModel.getAlbum());
        mRvGrid.setAdapter(mGridAdapter);

        /**
         * 1、假如已知列表高度的情况下，可以直接在布局中把RecycleView的高度定义上
         * 2、不知道列表高度的情况下，需要手动计算RecycleView的高度
         */
        mRvList = mContentView.findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mRvList.setNestedScrollingEnabled(false);//取消RecycleView的滑动
        mListAdapter = new MusicListAdapter(mContext,mRvList,mMusicSourceModel.getHot());
        mRvList.setAdapter(mListAdapter);
    }

    @OnClick({})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mRealmHelper.close();//不在使用数据时才能close掉
        EventBus.getDefault().unregister(this);//取消订阅
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageWrap event){

        A a = (A)event.getObject();
        Log.d("AAAAA",a.a + "   " + a.b);
        int oldP = DataUtils.getPos();
        int newP = Integer.parseInt(event.getMessage());
//        if(oldP == newP) return;
        Log.d("oldPosition",oldP+"");
        RecyclerView.LayoutManager layoutManager = mRvList.getLayoutManager();
        if(oldP != -1){
            View oldView= layoutManager.findViewByPosition(oldP);
            ImageView imageView = oldView.findViewById(R.id.iv_music_menu);
            imageView.setImageResource(R.mipmap.play);
        }

        if(newP != -1){
            View oldView= layoutManager.findViewByPosition(newP);
            ImageView imageView = oldView.findViewById(R.id.iv_music_menu);
            imageView.setImageResource(R.mipmap.play_now);
        }
        Log.d("newPosition",newP+"");
        DataUtils.setPos(newP);
    }

}
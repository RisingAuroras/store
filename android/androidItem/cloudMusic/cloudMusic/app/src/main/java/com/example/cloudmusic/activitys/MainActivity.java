package com.example.cloudmusic.activitys;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapters.MusicGridAdapter;
import com.example.cloudmusic.adapters.MusicListAdapter;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.MusicSourceModel;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.views.GridSpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity {

    private RecyclerView mRvGrid,mRvList;
    private MusicGridAdapter mGridAdapter;
    private MusicListAdapter mListAdapter;
    private RealmHelper mRealmHelper;
    private MusicSourceModel mMusicSourceModel;

    //按两下退出app
    private static final int TIME_EXIT=2000;
    private long mBackPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DataUtils.setPos(-1);
        EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅
        initData();
        initView();
    }

    private void initData(){
        mRealmHelper = new RealmHelper();
        mMusicSourceModel = mRealmHelper.getMusicSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelper.close();//不在使用数据时才能close掉
        EventBus.getDefault().unregister(this);//取消订阅
    }

    public void initView() {
        initNavBar(false,"dalidali",true);

        mRvGrid = findViewById(R.id.rv_grid);
        mRvGrid.setLayoutManager(new GridLayoutManager(this,3));
        mRvGrid.addItemDecoration(new GridSpaceItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.albumMarginSize),mRvGrid));
        mGridAdapter = new MusicGridAdapter(this,mMusicSourceModel.getAlbum());
        mRvGrid.setAdapter(mGridAdapter);

        /**
         * 1、假如已知列表高度的情况下，可以直接在布局中把RecycleView的高度定义上
         * 2、不知道列表高度的情况下，需要手动计算RecycleView的高度
         */
        mRvList = findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRvList.setNestedScrollingEnabled(false);//取消RecycleView的滑动
        mListAdapter = new MusicListAdapter(this,mRvList,mMusicSourceModel.getHot());
        mRvList.setAdapter(mListAdapter);

    }

    @Override
    public void onBackPressed() {
        if(mBackPressed+TIME_EXIT>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(this,"再点击一次返回退出程序",Toast.LENGTH_SHORT).show();
            mBackPressed=System.currentTimeMillis();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageWrap event){

        int oldP = DataUtils.getPos();
        int newP = Integer.parseInt(event.getMessage());
//        if(oldP == newP) return;
        Log.d("oldPosition",oldP+"");
        RecyclerView.LayoutManager layoutManager = mRvList.getLayoutManager();
        if(oldP != -1){
            View oldView= layoutManager.findViewByPosition(oldP);
            ImageView imageView = oldView.findViewById(R.id.iv_play);
            imageView.setImageResource(R.mipmap.play);
        }

        if(newP != -1){
            View oldView= layoutManager.findViewByPosition(newP);
            ImageView imageView = oldView.findViewById(R.id.iv_play);
            imageView.setImageResource(R.mipmap.play_now);
        }
        Log.d("newPosition",newP+"");
        DataUtils.setPos(newP);
    }
}
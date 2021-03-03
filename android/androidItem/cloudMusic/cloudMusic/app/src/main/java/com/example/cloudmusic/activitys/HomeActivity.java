package com.example.cloudmusic.activitys;



import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.constants.Constant;
import com.example.cloudmusic.fragment.MineFragment;
import com.example.cloudmusic.fragment.MusicFragment;
import com.example.cloudmusic.fragment.MusicFragment1;
import com.example.cloudmusic.helps.MediaPlayerHelp;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.livedata.LiveDataBus;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.Song;
import com.example.cloudmusic.services.MusicService;
import com.example.cloudmusic.test.A;
import com.example.cloudmusic.utils.BLog;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.GlideUtil;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.utils.MusicUtils;
import com.example.cloudmusic.utils.PerfectClickListener;
import com.example.cloudmusic.utils.SPUtils;
import com.example.cloudmusic.utils.StaticProgressMemo;
import com.example.cloudmusic.utils.StatusBarUtil;
import com.example.cloudmusic.utils.ToastUtil;
import com.example.cloudmusic.views.MusicRoundProgressView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.cloudmusic.constants.MusicConstant.CLOSE;
import static com.example.cloudmusic.constants.MusicConstant.NEXT;
import static com.example.cloudmusic.constants.MusicConstant.PAUSE;
import static com.example.cloudmusic.constants.MusicConstant.PLAY;
import static com.example.cloudmusic.constants.MusicConstant.PREV;
import static com.example.cloudmusic.constants.MusicConstant.PROGRESS;


public class HomeActivity extends BaseActivityAbstract {

    public static HomeActivity instance;
    // 头像URL
    public static final String IC_AVATAR = "https://clouddisc.oss-cn-hongkong.aliyuncs.com/image/ic_user.png?x-oss-process=style/thumb";
    public static final String TAG = "HomeActivity";

    public int musicState = 0; // 0 为 不播放 ， 1 为播放
    /**
     * 图片动画
     */
    private ObjectAnimator logoAnimation;

    /**
     * 自定义进度条
     */
    private MusicRoundProgressView musicProgress;

    /**
     * 音乐进度间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    private long exitTime = 0;
    /**
     * 时间
     */
    private long timeMillis;

    private View playControlView;//底部控制视图
    /**
     * 底部logo图标，点击之后弹出当前播放歌曲详情页
     */
    private ShapeableImageView ivLogo;
    /**
     * 底部当前播放歌名
     */
    private MaterialTextView tvDefaultName;

    private MusicService.MusicBind musicBinder;
    /**
     * 底部当前歌曲控制按钮, 播放和暂停
     */
    private ImageView btnPlay;
    /**
     * 底部当前歌曲控制 上一首
     */
    private ImageView btnPrev;
    /**
     * 底部当前歌曲控制 下一首
     */
    private ImageView btnNext;

    /**
     * 本地音乐数量
     */

    private TextView tvLocalMusicNum;

    private List<Song> mList;
    private List<MusicModel> musicList;

    /**
     * 列表位置
     */
    private int listPosition = 0;

    /**
     * 当Service中通知栏有变化时接收到消息
     */
    private LiveDataBus.BusMutableLiveData<String> activityLiveData;
    /**
     * 上下文参数
     */
    protected Activity context;
    /**
     * 当在Activity中做出播放状态的改变时，通知做出相应改变
     */
    private LiveDataBus.BusMutableLiveData<String> notificationLiveData;


    private LinearLayout layLocalMusic;

    MediaPlayerHelp mediaPlayerHelp;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.vp_content)
    ViewPager vp_content;

    private List<Fragment> mFragment;
    @BindView(R.id.iv_title_one)
    ImageView ivTitleOne;
    @BindView(R.id.iv_title_two)
    ImageView ivTitleTwo;
    @BindView(R.id.iv_title_three)
    ImageView ivTitleThree;

    private MusicService musicService;


    @Override
    protected int getLayoutId() {
        instance = this;
        return R.layout.activity_home_layout;
    }

    @Override
    protected void initView() {
        this.context = this;
        super.context = this;

        if(isServiceExisted(MusicService.class.getName())){//设置服务是否在运行
            StaticProgressMemo.setIsRunning(true);
        }
        else {
            StaticProgressMemo.setIsRunning(false);
        }

        initNavBar(false,"",false);

        ivLogo = findViewById(R.id.play_control_layout).findViewById(R.id.iv_logo);
        tvDefaultName = findViewById(R.id.play_control_layout).findViewById(R.id.tv_default_name);
        btnPlay =findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_play);
        btnPrev = findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_pre);
        btnNext = findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_next);
        musicProgress = findViewById(R.id.play_control_layout).findViewById(R.id.music_progress);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicState ^= 1;

                if(musicState == 1){
                    btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                    EventBus.getDefault().post(MessageWrap.
                            getInstance(0,"PlayMusic",null));//EventBus发布消息,用来展示播放状态
                }
                else {
                    btnPlay.setImageResource(R.drawable.play_black);
                    EventBus.getDefault().post(MessageWrap.
                            getInstance(0,"PauseMusic",null));//EventBus发布消息,用来展示播放状态
                }


            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(MessageWrap.getInstance(0,"PrevMusic",null));//EventBus发布消息,用来展示播放状态

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(MessageWrap.getInstance(0,"NextMusic",null));//EventBus发布消息,用来展示播放状态
            }
        });

        initAnimation();//底部转动动画

        //绑定服务
        Intent serviceIntent = new Intent(context, MusicService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        ButterKnife.bind(this);
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawerLayout,
                this.getResources().getColor(R.color.colorTheme));
        initDrawerLayout();
        initContentFragment();
    }
    /**
     * 服务连接
     */
    private ServiceConnection connection = new ServiceConnection() {

        /**
         * 连接服务
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBind) service;
            musicService = musicBinder.getService();
            BLog.d(TAG, "Service与Activity已连接");

        }

        //断开服务
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBinder = null;
        }
    };
    @Override
    protected void initData() {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅


    }

    private void initDrawerLayout() {
        navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navView.getHeaderView(0);
        ImageView iv_avatar = headerView.findViewById(R.id.iv_avatar);
        GlideUtil.displayCircle(iv_avatar, IC_AVATAR);
        LinearLayout ll_nav_account = headerView.findViewById(R.id.ll_nav_account);
        ll_nav_account.setOnClickListener(listener);
        LinearLayout ll_nav_password = headerView.findViewById(R.id.ll_nav_password);
        ll_nav_password.setOnClickListener(listener);
        LinearLayout ll_nav_feedback = headerView.findViewById(R.id.ll_nav_feedback);
        ll_nav_feedback.setOnClickListener(listener);
        LinearLayout ll_nav_version_update = headerView.findViewById(R.id.ll_nav_version_update);
        ll_nav_version_update.setOnClickListener(listener);
        LinearLayout ll_nav_score = headerView.findViewById(R.id.ll_nav_score);
        ll_nav_score.setOnClickListener(listener);
        LinearLayout ll_nav_account_switch = headerView.findViewById(R.id.ll_nav_account_switch);
        ll_nav_account_switch.setOnClickListener(listener);

        LinearLayout ll_nav_logout = headerView.findViewById(R.id.ll_nav_logout);
        ll_nav_logout.setOnClickListener(this::onClick);

    }

    private PerfectClickListener listener = new PerfectClickListener() {

        @Override
        protected void onNoDoubleClick(final View v) {
            drawerLayout.closeDrawer(GravityCompat.START);
            drawerLayout.postDelayed(() -> {
                switch (v.getId()) {
                    case R.id.ll_nav_account:
                        ToastUtil.showToast("个人中心");
                        Intent accountIntent = new Intent(HomeActivity.this, TestActivity.class);
                        accountIntent.putExtra("text", "个人中心");
                        startActivity(accountIntent);
                        break;
                    case R.id.ll_nav_password:
                        ToastUtil.showToast("密码设置");
                        Intent passwordIntent = new Intent(HomeActivity.this, TestActivity.class);
                        passwordIntent.putExtra("text", "密码设置");
                        startActivity(passwordIntent);
                        break;
                    case R.id.ll_nav_feedback:
                        ToastUtil.showToast("意见反馈");
                        Intent feedbackIntent = new Intent(HomeActivity.this, TestActivity.class);
                        feedbackIntent.putExtra("text", "意见反馈");
                        startActivity(feedbackIntent);
                        break;
                    case R.id.ll_nav_version_update:
                        ToastUtil.showToast("版本更新");
                        Intent updateIntent = new Intent(HomeActivity.this, TestActivity.class);
                        updateIntent.putExtra("text", "版本更新");
                        startActivity(updateIntent);
                        break;
                    case R.id.ll_nav_score:
                        ToastUtil.showToast("给个评分呗");
                        Intent scoreIntent = new Intent(HomeActivity.this, TestActivity.class);
                        scoreIntent.putExtra("text", "给个评分呗");
                        startActivity(scoreIntent);
                        break;
                    case R.id.ll_nav_account_switch:
                        ToastUtil.showToast("切换账号");
                        Intent switchIntent = new Intent(HomeActivity.this, TestActivity.class);
                        switchIntent.putExtra("text", "切换账号");
                        startActivity(switchIntent);
                        break;
                    default:
                        break;
                }
            }, 260);
        }
    };

    private void initContentFragment() {

        mFragment = new ArrayList<>();
        mFragment.add(new MusicFragment1());
        mFragment.add(new MusicFragment());
        mFragment.add(new MineFragment());
        //预加载最多的数量
        vp_content.setOffscreenPageLimit(2);
        //设置适配器
        vp_content.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }
        });
        // 设置默认加载第2个Fragment
        setCurrentItem(1);
        // ViewPager的滑动监听
        vp_content.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    /**
     * 判断service是否已经运行
     * 必须判断uid,因为可能有重名的Service,所以要找自己程序的Service,不同进程只要是同一个程序就是同一个uid,个人理解android系统中一个程序就是一个用户
     * 用pid替换uid进行判断强烈不建议,因为如果是远程Service的话,主进程的pid和远程Service的pid不是一个值,在主进程调用该方法会导致Service即使已经运行也会认为没有运行
     * 如果Service和主进程是一个进程的话,用pid不会出错,但是这种方法强烈不建议,如果你后来把Service改成了远程Service,这时候判断就出错了
     *
     * @param className Service的全名,例如PushService.class.getName()
     * @return true:Service已运行 false:Service未运行
     */

    public boolean isServiceExisted(String className) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }


    @OnClick({R.id.ll_title_menu, R.id.iv_title_one, R.id.iv_title_two, R.id.iv_title_three})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_title_menu:
                // 开启抽屉式菜单
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_title_one:
                // 这样做的目的是减少cpu的损耗
                if (vp_content.getCurrentItem() != 0) {
                    setCurrentItem(0);
                }
                break;
            case R.id.iv_title_two:
                if (vp_content.getCurrentItem() != 1) {
                    setCurrentItem(1);
                }
                break;
            case R.id.iv_title_three:
                if (vp_content.getCurrentItem() != 2) {
                    setCurrentItem(2);
                }
                break;
            case R.id.ll_nav_logout:
                // 退出登录
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 切换页面
     *
     * @param position 分类角标
     */
    private void setCurrentItem(int position) {
        boolean isOne = false;
        boolean isTwo = false;
        boolean isThree = false;
        switch (position) {
            case 0:
                isOne = true;
                break;
            case 1:
                isTwo = true;
                break;
            case 2:
                isThree = true;
                break;
            default:
                isTwo = true;
                break;
        }
        vp_content.setCurrentItem(position);
        ivTitleOne.setSelected(isOne);
        ivTitleTwo.setSelected(isTwo);
        ivTitleThree.setSelected(isThree);
    }


    /**
     * 初始化动画
     */
    private void initAnimation() {
        logoAnimation = ObjectAnimator.ofFloat(ivLogo, "rotation", 0.0f, 360.0f);
        logoAnimation.setDuration(10000);
        logoAnimation.setInterpolator(new LinearInterpolator());
        logoAnimation.setRepeatCount(-1);
        logoAnimation.setRepeatMode(ObjectAnimator.RESTART);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageWrap event){
        if(event.getMessage().equals("musicList")){
            listPosition = event.getInteger();
//            Log.d(TAG,"位置 " + listPosition);
            musicList = (List<MusicModel>) event.getObject();

            MusicModel model = musicList.get(listPosition);

            Glide.with(context).load(model.getPoster())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivLogo);
            tvDefaultName.setText(model.getName());
            //图片旋转动画
            logoAnimation.start();
            musicState = 1;
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);

            updateProgress();
        }
        else if(event.getMessage().equals("changeMusic")){ //改变音乐
            listPosition = event.getInteger();
            MusicModel model = musicList.get(listPosition);

            Glide.with(context).load(model.getPoster())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivLogo);
            tvDefaultName.setText(model.getName());
            //图片旋转动画
            logoAnimation.start();
            musicState = 1;
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);

            updateProgress();
        }
        else if(event.getMessage().equals("AnswerMusicProgress")){
            int progress = event.getInteger();
            int total = (Integer)event.getObject();
            Log.d(TAG,progress + " " + total);
            if(progress >= total-1000){//播完这首，发送 next 请求
                EventBus.getDefault().post(MessageWrap.getInstance(0,"NextMusic",null));//EventBus发布消息,用来展示播放状态

            }
            musicProgress.setProgress(progress, total);

            Message msg = Message.obtain();
            mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
        }
        else if(event.getMessage().equals("UIControl")){
            String state = (String)event.getObject();
            String msg = "";
            if(state.equals("play")){
                musicState = 1;
                btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                EventBus.getDefault().post(MessageWrap.
                        getInstance(0,"PlayMusic",null));//EventBus发布消息,用来展示播放状态
            }
            else if(state.equals("pause")){
                musicState = 0;
                btnPlay.setImageResource(R.drawable.play_black);
                EventBus.getDefault().post(MessageWrap.
                        getInstance(0,"PauseMusic",null));//EventBus发布消息,用来展示播放状态
            }
            else if(state.equals("prev")){
                EventBus.getDefault().post(MessageWrap.getInstance(0,"PrevMusic",null));//EventBus发布消息,用来展示播放状态
            }
            else if(state.equals("next")){
                EventBus.getDefault().post(MessageWrap.getInstance(0,"NextMusic",null));//EventBus发布消息,用来展示播放状态

            }
        }
    }
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            EventBus.getDefault().post(MessageWrap.
                    getInstance(-1,"RequrieMusicProgress",null));//EventBus发布消息,用来展示播放状态
            return true;
        }
    });

    /**
     * 更新进度
     */
    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        // 获取一个现成的消息
        Message msg = Message.obtain();
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        int progress = 0;
        msg.arg1 = progress;
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToast("再按一次退出应用");
                exitTime = System.currentTimeMillis();
            } else {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消订阅
    }
}

package com.example.cloudmusic.activitys;



import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cloudmusic.R;
import com.example.cloudmusic.fragment.MineFragment;
import com.example.cloudmusic.fragment.MusicFragment;
import com.example.cloudmusic.fragment.MusicFragment1;
import com.example.cloudmusic.utils.GlideUtil;
import com.example.cloudmusic.utils.PerfectClickListener;
import com.example.cloudmusic.utils.StatusBarUtil;
import com.example.cloudmusic.utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivityAbstract {

    public static HomeActivity instance;
    // 头像URL
    public static final String IC_AVATAR = "https://clouddisc.oss-cn-hongkong.aliyuncs.com/image/ic_user.png?x-oss-process=style/thumb";

    private long exitTime = 0;

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

    @Override
    protected int getLayoutId() {
        instance = this;
        return R.layout.activity_home_layout;
    }

    @Override
    protected void initView() {
        initNavBar(false,"",false);

        ButterKnife.bind(this);
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawerLayout,
                this.getResources().getColor(R.color.colorTheme));
        initDrawerLayout();
        initContentFragment();
    }

    @Override
    protected void initData() {

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

}

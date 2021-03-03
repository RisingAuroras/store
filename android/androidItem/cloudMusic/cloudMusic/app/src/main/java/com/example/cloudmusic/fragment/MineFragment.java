package com.example.cloudmusic.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activitys.LocalMusicActivity;
import com.example.cloudmusic.constants.Constant;
import com.example.cloudmusic.utils.SPUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends Fragment {

    private View mContentView;
    private Unbinder unbinder;
    private Activity mContext;

    private LinearLayout layLocalMusic;
    /**
     * 本地音乐数量
     */
    private TextView tvLocalMusicNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        initData();
        return mContentView;
    }

    private void initView() {
        unbinder = ButterKnife.bind(this, mContentView);
        layLocalMusic = mContentView.findViewById(R.id.lay_local_music);
        tvLocalMusicNum = mContentView.findViewById(R.id.tv_local_music_num);
    }

    private void initData() {

    }

    @OnClick({R.id.lay_local_music})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_local_music:
                startActivity(new Intent(mContext, LocalMusicActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int num = SPUtils.getInt(Constant.LOCAL_MUSIC_NUM,0,mContext);
        tvLocalMusicNum.setText(num+"");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
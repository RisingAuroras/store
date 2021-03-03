package com.example.cloudmusic.views;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    public GridSpaceItemDecoration(int space,RecyclerView parent) {
        mSpace = space;
        getRecyclerViewoffsets(parent);
    }

    /**
     *
     * @param outRect Item的矩形边界
     * @param view  ItemView
     * @param parent RecycleView
     * @param state ReycleView的状态
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.left = mSpace;
    }

    private void getRecyclerViewoffsets (RecyclerView recyclerView) {
        //判断Item是不是每一行第一个Item
//        if (parent.getChildLayoutPosition(view) % 3 == 0){
//            outRect.left = 0;
        //不可行，是因为整体会高出其他
//        }
        //View margin,
        //margin 为正值， 则View 会距离边界产生一个距离
        //margin 为负值， 则View 会超出边界产生一个距离

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)recyclerView.getLayoutParams();
        layoutParams.leftMargin = -mSpace;
        recyclerView.setLayoutParams(layoutParams);
    }
}

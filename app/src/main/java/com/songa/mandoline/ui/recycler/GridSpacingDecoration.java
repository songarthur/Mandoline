package com.songa.mandoline.ui.recycler;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class GridSpacingDecoration extends RecyclerView.ItemDecoration
{
    private int spacing;
    private int halfSpacing;

    public GridSpacingDecoration(int spacingInDp, @NonNull Resources resources)
    {
        this.spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacingInDp, resources.getDisplayMetrics());
        this.halfSpacing = spacing/2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        /*
        if (parent.getLayoutManager()!=null && parent.getLayoutManager() instanceof GridLayoutManager) {

            GridLayoutManager lm = (GridLayoutManager) parent.getLayoutManager();
            int span = lm.getSpanCount();
            int pos = parent.getChildLayoutPosition(view);
            int rowPos = pos%span;

            outRect.top = pos<span ? spacing : 0;
            outRect.bottom = spacing;
            outRect.left = rowPos==0 ? spacing : halfSpacing;
            outRect.right = rowPos==(span-1) ? spacing : halfSpacing;

        } else {
            outRect.top = halfSpacing;
            outRect.bottom = halfSpacing;
            outRect.left = halfSpacing;
            outRect.right = halfSpacing;
        }*/

        outRect.top = halfSpacing;
        outRect.bottom = halfSpacing;
        outRect.left = halfSpacing;
        outRect.right = halfSpacing;
    }
}

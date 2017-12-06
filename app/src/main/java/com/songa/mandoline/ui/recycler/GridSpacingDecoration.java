package com.songa.mandoline.ui.recycler;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Recycler decoration that adds spacing between items.
 */
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
        outRect.top = halfSpacing;
        outRect.bottom = halfSpacing;
        outRect.left = halfSpacing;
        outRect.right = halfSpacing;
    }
}

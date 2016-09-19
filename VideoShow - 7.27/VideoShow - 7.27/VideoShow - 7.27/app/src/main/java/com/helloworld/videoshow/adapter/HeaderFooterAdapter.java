package com.helloworld.videoshow.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.helloworld.videoshow.utils.Tool;

import java.util.List;

/**
 * Created by Hello on 2016/7/25.
 */
public abstract class HeaderFooterAdapter<T> extends BaseAdapter<T> {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;

    protected Context mContext;
    protected View mHeaderView;
    protected View mFooterView;

    public HeaderFooterAdapter(Context context, List<T> datas) {
        super(datas);
        mContext = context;
    }

    public void addHeaderView(View headerView) {
        if (mHeaderView == null) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }
    }

    public void removeHeaderView() {
        if (mHeaderView != null) {
            mHeaderView = null;
            notifyItemRemoved(0);
        }
    }

    public void addFooterView(View footerView) {
        if (mFooterView == null) {
            mFooterView = footerView;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeFooterView() {
        if (mFooterView != null) {
            mFooterView = null;
            notifyItemRemoved(getItemCount() - 1);
        }
    }

    public boolean hasFooter() {
        return mFooterView != null;
    }

    public boolean hasHeader() {
        return mHeaderView != null;
    }


    public boolean isFooter(int position) {
        if(hasFooter()) {
            if(hasHeader()) {
                if(position >= getItemCount() - 2) {
                    return true;
                }
            } else {
                if(position >= getItemCount() - 1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean isHeader(int position) {
        if(hasHeader() && position == 0) {
            return true;
        }
        return false;
    }

    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {

        public HeaderFooterViewHolder(Context context, View itemView) {
            super(itemView);
            int width = Tool.getWidth(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }
    }


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new HeaderFooterViewHolder(mContext, mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new HeaderFooterViewHolder(mContext, mFooterView);
        }
        return onCreate(parent, viewType);
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        }
        onBind(holder, position);
    }

    public abstract void onBind(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && mFooterView != null) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    return viewType == TYPE_FOOTER || viewType == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            int position = holder.getLayoutPosition();
            int viewType = getItemViewType(position);
            if(viewType == TYPE_FOOTER || viewType == TYPE_HEADER) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public T getDataByPosition(int position) {
        return super.getDataByPosition(getRealPosition(position));
    }

    public int getRealPosition(int position) {
        if (position > 0 && mHeaderView != null) {
            return position - 1;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mHeaderView != null) {
            itemCount++;
        }
        if (getDatas() != null && getDatas().size() > 0) {
            itemCount = itemCount + getDatas().size();
        }
        if (mFooterView != null) {
            itemCount++;
        }
        return itemCount;
    }

}
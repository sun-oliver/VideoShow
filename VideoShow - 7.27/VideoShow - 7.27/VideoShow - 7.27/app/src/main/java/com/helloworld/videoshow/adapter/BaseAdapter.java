package com.helloworld.videoshow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hello on 2016/7/25.
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ADD = -100;
    public static final int UPDATE = -101;

    protected List<T> mDatas;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    protected OnItemClick mOnItemClick;
    protected OnLongItemClick mOnLongItemClick;

    protected boolean mEditMode;
    protected boolean[] mChecked;

    public BaseAdapter(List<T> datas) {
        mDatas = datas;
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mOnItemClick = new OnItemClick(this, holder, position);
        mOnLongItemClick = new OnLongItemClick(this, holder, position);
    }

    public void setDatas(List<T> datas, int updateType) {
        if (updateType == ADD) {
            mDatas.addAll(datas);
            notifyItemRangeChanged(mDatas.size(), datas.size());
        } else {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
        if(isEditMode()) {
            mChecked = new boolean[mDatas.size()];
        }
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public T getDataByPosition(int position) {
        return mDatas.get(position);
    }


    public boolean isEditMode() {
        return mEditMode;
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if(mEditMode) {
            mChecked = new boolean[mDatas.size()];
        }
    }

    public boolean isChecked(int position) {
        if(mChecked == null || mChecked.length <= 0) {
            return false;
        }
        return mChecked[position];
    }

    public void checked(int position, boolean checked) {
        mChecked[position] = checked;
        notifyItemChanged(position);
    }

    public void checkedAll() {
        if(mChecked != null && mChecked.length > 0) {
            for(int i = 0; i < mChecked.length; i++) {
                mChecked[i] = true;
            }
        }
        notifyDataSetChanged();
    }

    public boolean isAllChecked() {
        if(mChecked != null && mChecked.length > 0) {
            for(int i = 0; i < mChecked.length; i++) {
                if(!mChecked[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void uncheckedAll() {
        if(mChecked != null && mChecked.length > 0) {
            for(int i = 0; i < mChecked.length; i++) {
                mChecked[i] = false;
            }
        }
        notifyDataSetChanged();
    }

    public int getCheckedNum() {
        int num = 0;
        if(mChecked != null && mChecked.length > 0) {
            for(int i = 0; i < mChecked.length; i++) {
                if(mChecked[i]) {
                    num++;
                }
            }
        }
        return num;
    }

    public List<T> getCheckedDatas() {
        if(mChecked == null || mChecked.length <= 0) {
            return null;
        }
        List<T> checkedDatas = new ArrayList<>();
        for(int i = 0; i < mChecked.length; i++) {
            if(mChecked[i]) {
                checkedDatas.add(mDatas.get(i));
            }
        }
        return checkedDatas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }


    public interface OnItemClickListener {

        void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    private static class OnItemClick implements View.OnClickListener {

        WeakReference<BaseAdapter> wr;
        RecyclerView.ViewHolder holder;
        int position;

        public OnItemClick(BaseAdapter adapter, RecyclerView.ViewHolder holder, int position) {
            wr = new WeakReference<>(adapter);
            this.holder = holder;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(wr == null) {
                return;
            }
            BaseAdapter adapter = wr.get();
            if(adapter == null) {
                return;
            }
            if(adapter.mOnItemClickListener != null) {
                adapter.mOnItemClickListener.onItemClick(holder, v, position);
            }
        }
    }

    private static class OnLongItemClick implements View.OnLongClickListener {

        WeakReference<BaseAdapter> wr;
        RecyclerView.ViewHolder holder;
        int position;

        public OnLongItemClick(BaseAdapter adapter, RecyclerView.ViewHolder holder, int position) {
            wr = new WeakReference<>(adapter);
            this.holder = holder;
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v) {
            if(wr == null) {
                return false;
            }
            BaseAdapter adapter = wr.get();
            if(adapter == null) {
                return false;
            }
            if(adapter.mOnItemLongClickListener == null) {
                return false;
            }
            adapter.mOnItemLongClickListener.onItemLongClick(holder, v, position);
            return true;
        }
    }
}

package com.helloworld.videoshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helloworld.videoshow.R;
import com.helloworld.videoshow.model.VideoInfo;
import com.helloworld.videoshow.services.DownloadService;
import com.helloworld.videoshow.utils.DataUtils;
import com.helloworld.videoshow.utils.PreferencesUtil;
import com.helloworld.videoshow.utils.Tool;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hello on 2016/7/12.
 */
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = "MainListAdapter";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private View mHeaderView = null;
    private View mFooterView = null;

    private List<VideoInfo> list;
    private Context mContext;
    private int[] resId = {
            R.drawable.icon_video_1,
            R.drawable.icon_video_2,
            R.drawable.icon_video_3,
            R.drawable.icon_video_4,
            R.drawable.icon_video_5,
            R.drawable.icon_video_6,
            R.drawable.icon_video_7,
            R.drawable.icon_video_8,
            R.drawable.icon_video_9,
            R.drawable.icon_video_play};

    public MainListAdapter(Context context, ArrayList<VideoInfo> mVideoInfo) {
        mContext = context;
        list = mVideoInfo;
    }

    //定义接口，提供点击事件
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    protected OnItemClick mOnItemClick;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private class OnItemClick implements View.OnClickListener {
        RecyclerView.ViewHolder holder;
        int position;

        public OnItemClick(RecyclerView.ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position);
            }
        }
    }

    //更新下载进度
    public void updateProgress(String id, long progress, long size) {
        for (int i = 0, j = list.size(); i < j; i++) {
            VideoInfo videoInfo = list.get(i);
            if (videoInfo.getUuid().equals(id)) {
                videoInfo.setFinished((int) progress);
                if (videoInfo.getSize() == 0 && size > 0) {
                    videoInfo.setSize(size);
                }
                notifyDataSetChanged();
                break;
            }
        }
    }

    //添加视频信息
    public void add(VideoInfo mVideoInfo) {
        this.list.clear();
        this.list.add(mVideoInfo);
        Log.e(TAG, "list size" + list.size());
        notifyDataSetChanged();
    }

    //添加视频信息
    public void add(List<VideoInfo> list) {
        this.list.clear();
        this.list.addAll(list);
        Log.e(TAG, "list size" + list.size());
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.list.clear();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        //notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        //notifyItemInserted(0);
    }

    public View getFooterView() {
        return mFooterView;
    }

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            View headerView = mHeaderView;
            HeadFootViewHolder headerHolder = new HeadFootViewHolder(headerView);
            return headerHolder;
        } else if (mFooterView != null && viewType == TYPE_FOOTER) {
            View footerView = mFooterView;
            HeadFootViewHolder footerHolder = new HeadFootViewHolder(mFooterView);
            return footerHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            MyViewHolder holder = new MyViewHolder(Tool.getWidth(mContext), view);
            view.setTag(holder);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, final int pos) {
        if (getItemViewType(pos) == TYPE_HEADER){
           return;
        }
        if (getItemViewType(pos) == TYPE_FOOTER)
            return;
        MyViewHolder holder = (MyViewHolder) mholder;
        final int position = getRealPositon(holder);
        if(mOnItemClickListener == null) return;
        String videoPath = list.get(position).getThumb();
        if (videoPath != null) {
            Uri uri = Uri.parse(videoPath);
            Picasso.with(mContext)
                    .load(uri)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.icon_background)
                    .error(R.drawable.icon_video_background_big)
                    .into(holder.ivBackground);
        }

        /**
         * 判断是否正在下载，
         * 正在下载，隐藏数字，显示进度条
         * 下载完成显示播放按钮，隐藏进度条
         */
        Log.e("yjg","position:"+position+"   status:" +list.get(position).getDownloadStatus());
        if(PreferencesUtil.getWhichDownloaded(mContext).contains(position+"")){
            holder.ivDownloadButton.setVisibility(View.VISIBLE);
            holder.ivDownloadButton.setImageResource(resId[9]);
            holder.tvDownloadRatio.setVisibility(View.GONE);
            holder.pbDownloadVideo.setVisibility(View.GONE);
        }else {
            if (list.get(position).getDownloadStatus() == 0) {
                holder.ivDownloadButton.setVisibility(View.VISIBLE);
                holder.ivDownloadButton.setImageResource(resId[position]);
                holder.tvDownloadRatio.setVisibility(View.GONE);
                holder.pbDownloadVideo.setVisibility(View.GONE);
            } else if (list.get(position).getDownloadStatus() == 1) {
                holder.ivDownloadButton.setVisibility(View.GONE);
                holder.tvDownloadRatio.setVisibility(View.VISIBLE);
                holder.pbDownloadVideo.setVisibility(View.VISIBLE);
            } else if (list.get(position).getDownloadStatus() == 2) {
                holder.ivDownloadButton.setVisibility(View.VISIBLE);
                holder.ivDownloadButton.setImageResource(resId[9]);
                holder.tvDownloadRatio.setVisibility(View.GONE);
                holder.pbDownloadVideo.setVisibility(View.GONE);
            }
        }
        holder.txtVideoLength.setText(list.get(position).getDuration() + "s");
        holder.txtVideoTitle.setText(list.get(position).getDescription());
        String strShareNum = mContext.getResources().getString(R.string.share_count);
        //分享次数
        String shareNum = String.format(strShareNum, DataUtils.shareNum()+"");
        holder.videoShareNum.setText(shareNum);

        holder.tvDownloadRatio.setText(list.get(position).getFinished() + "%");
        holder.pbDownloadVideo.setProgress((int) list.get(position).getFinished());

        if(PreferencesUtil.getWhichLike(mContext).contains(position+"")){
            holder.ivLike.setSelected(true);
        }else {
            holder.ivLike.setSelected(list.get(position).getLike());
        }


        mOnItemClick = new OnItemClick(holder, pos);//pos范围1-9
        //回调事件
        holder.flVideoControl.setOnClickListener(mOnItemClick);
        holder.ivLike.setOnClickListener(mOnItemClick);
        holder.rlWhatsapp.setOnClickListener(mOnItemClick);
        holder.ivShare.setOnClickListener(mOnItemClick);
    }

    public int getRealPositon(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int headerfootNum = 0;
        if (mHeaderView != null) {
            headerfootNum++;
        }
        if (mFooterView != null) {
            headerfootNum++;
        }
        return list.size() + headerfootNum;
    }

    public class HeadFootViewHolder extends RecyclerView.ViewHolder{

        public HeadFootViewHolder(View itemView) {
            super(itemView);
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlVideoLayout;
        private ImageView ivBackground;
        private TextView txtVideoLength;
        private TextView txtVideoTitle;
        private TextView videoShareNum;

        public FrameLayout flVideoControl;
        public ImageView ivDownloadButton;
        public ProgressBar pbDownloadVideo;
        public TextView tvDownloadRatio;

        public ImageView ivLike, ivShare;
        public RelativeLayout rlWhatsapp;

        public MyViewHolder(int parentWidth, View itemView) {
            super(itemView);
            if (itemView == mHeaderView || itemView == mFooterView) {
                int width = parentWidth;
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(lp);
                return;
            }
            /*if(itemView == mFooterView)
                return;*/
            rlVideoLayout = (RelativeLayout) itemView.findViewById(R.id.rl_video_layout);
            //设置视频长宽1:1
            ViewGroup.LayoutParams lp = rlVideoLayout.getLayoutParams();
            lp.width = parentWidth;
            lp.height = parentWidth;
            rlVideoLayout.setLayoutParams(lp);

            ivBackground = (ImageView) itemView.findViewById(R.id.image_bg);
            flVideoControl = (FrameLayout) itemView.findViewById(R.id.fl_control_video_btn);
            ivDownloadButton = (ImageView) itemView.findViewById(R.id.image_download_btn);
            pbDownloadVideo = (ProgressBar) itemView.findViewById(R.id.pb_video_downloading);
            tvDownloadRatio = (TextView) itemView.findViewById(R.id.tv_download_ratio);

            txtVideoLength = (TextView) itemView.findViewById(R.id.tv_video_length);
            txtVideoTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
            videoShareNum = (TextView) itemView.findViewById(R.id.tv_share_num);

            rlWhatsapp = (RelativeLayout) itemView.findViewById(R.id.rl_whatsapp);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
            ivShare = (ImageView) itemView.findViewById(R.id.iv_share);
        }
    }

}

package com.helloworld.videoshow.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.helloworld.videoshow.MyApplication;
import com.helloworld.videoshow.R;
import com.helloworld.videoshow.VideoPlayActivity;
import com.helloworld.videoshow.adapter.MainListAdapter;
import com.helloworld.videoshow.model.VideoInfo;
import com.helloworld.videoshow.model.VideoListData;
import com.helloworld.videoshow.services.DownloadService;
import com.helloworld.videoshow.utils.Const;
import com.helloworld.videoshow.utils.DataUtils;
import com.helloworld.videoshow.utils.PreferencesUtil;
import com.helloworld.videoshow.utils.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private static String TAG = "VideoFragment";
    private LocalBroadcastManager broadcastManager;
    private Context context;//上下文对象
    private RecyclerView recyclerView_mainlist;

    public MainListAdapter mMainListAdapter;
    private RelativeLayout rlNoInternet;
    private Button btnRefresh;

    public VideoListData listData;
    private String mp4FullPath;//mp4的url路径，打点用
    private boolean isWhatsapp = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        recyclerView_mainlist = (RecyclerView) view.findViewById(R.id.recyclerView_mainlist);
        rlNoInternet = (RelativeLayout) view.findViewById(R.id.rl_no_internet);
        btnRefresh = (Button) view.findViewById(R.id.btn_no_internet_refresh);
        if (Tool.isNetConnected(context)) {
            recyclerView_mainlist.setVisibility(View.VISIBLE);
            rlNoInternet.setVisibility(View.GONE);
        } else {
            recyclerView_mainlist.setVisibility(View.GONE);
            rlNoInternet.setVisibility(View.VISIBLE);
        }

        recyclerView_mainlist.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_mainlist.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 0;
                outRect.bottom = 0;
                outRect.left = 0;
                outRect.right = 0;
            }
        });

        mMainListAdapter = new MainListAdapter(context, new ArrayList<VideoInfo>());
        recyclerView_mainlist.setAdapter(mMainListAdapter);

        String getJsonResult = PreferencesUtil.getJsonData(context);

        Log.e(TAG, "getJsonFromSharePreference" + getJsonResult);
        if (getJsonResult != "") {
            listData = new Gson().fromJson(getJsonResult, VideoListData.class);
            Log.e(TAG, "listData:" + listData);
            mMainListAdapter.add(listData.getVideoList());
        } else {
            getJsonFromDataBase();
        }

        //添加头部底部
        setHeader(recyclerView_mainlist);
        setFooter(recyclerView_mainlist);
        //获取广播管理者
        broadcastManager = LocalBroadcastManager.getInstance(context);

        initEvent();
        registerDownloadReceiver();


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tool.isNetConnected(context)) {
                    recyclerView_mainlist.setVisibility(View.VISIBLE);
                    rlNoInternet.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void setHeader(RecyclerView view) {
        View viewHeader = LayoutInflater.from(context).inflate(R.layout.layout_recyclerview_header, view, false);
        TextView tvSeeNum = (TextView) viewHeader.findViewById(R.id.tv_how_many_people_see);
        String strShareNum = context.getResources().getString(R.string.tv_how_many_people_see);
        //总查看次数
        String shareNum = String.format(strShareNum, DataUtils.TotalSeeNum()+"");
        tvSeeNum.setText(shareNum);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewHeader.setLayoutParams(lp);
        mMainListAdapter.setHeaderView(viewHeader);
    }

    private void setFooter(RecyclerView view) {
        View viewFooter = LayoutInflater.from(context).inflate(R.layout.layout_recyclerview_footer, view, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Tool.dp2px(context, 100));
        viewFooter.setLayoutParams(lp);
        mMainListAdapter.setFooterView(viewFooter);
    }

    private void getJsonFromDataBase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //URL url = new URL(HTTP_URL + Tool.getCurrentTime() +"/"+Tool.getCurrentCountry(context));
                    URL url = new URL(Const.HTTP_URL + Tool.getCurrentTime() + "/" + Tool.getCurrentCountry(context));
                    Log.e(TAG, "url" + url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(3000);
                    connection.setConnectTimeout(3000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String result = response + "";
                    Log.e(TAG, "data:" + result);
                    listData = new Gson().fromJson(result, VideoListData.class);
                    mMainListAdapter.add(listData.getVideoList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initEvent() {
        mMainListAdapter.setOnItemClickListener(new MainListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Tool.isNetConnected(context)) {
                    switch (view.getId()) {
                        case R.id.fl_control_video_btn: {
                            //获取界面上的view要考虑header
                            //获取实际的数据需要 -1
                            int dataPosition = position - 1;
                            View itemView = recyclerView_mainlist.getLayoutManager().findViewByPosition(position);
                            final MainListAdapter.MyViewHolder holder = (MainListAdapter.MyViewHolder) itemView.getTag();
                            //如果未下载，先下载
                            mp4FullPath = listData.getVideoList().get(dataPosition).getFilepath();
                            String mp4name = mp4FullPath.substring(mp4FullPath.lastIndexOf('/'), mp4FullPath.length());
                            if (!checkIsDownLoad(mp4name)) {
                                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                                        .setCategory("ShouYe")
                                        .setAction(mp4FullPath+":"+"XiaZai")
                                        .build());
                                holder.ivDownloadButton.setVisibility(View.GONE);
                                holder.tvDownloadRatio.setVisibility(View.VISIBLE);
                                holder.pbDownloadVideo.setVisibility(View.VISIBLE);
                                listData.getVideoList().get(dataPosition).setDownloadStatus(1);

                                Intent intent = new Intent(context, DownloadService.class);
                                intent.setAction(DownloadService.ACTION_START);
                                intent.putExtra("videoInfo", listData.getVideoList().get(dataPosition));
                                intent.putExtra("position", dataPosition);
                                context.startService(intent);
                            } else {
                                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                                        .setCategory("ShouYe")
                                        .setAction(mp4FullPath+":"+"BoFang")
                                        .build());
                                Intent intentVideo1 = new Intent(context, VideoPlayActivity.class);
                                intentVideo1.putExtra("videoUrl", Tool.getDownloadDir(context) + mp4name);
                                startActivity(intentVideo1);
                            }
                            break;
                        }
                        case R.id.rl_whatsapp:
                            MyApplication.tracker().send(new HitBuilders.EventBuilder()
                                    .setCategory("ShouYe")
                                    .setAction(mp4FullPath+":"+"WhatsappButton")
                                    .build());
                            shareWhatsApp(position);
                            break;
                        case R.id.iv_like:
                            MyApplication.tracker().send(new HitBuilders.EventBuilder()
                                    .setCategory("ShouYe")
                                    .setAction(mp4FullPath+":"+"DianZan")
                                    .build());
                            if (view.isSelected()) {
                                Log.e(TAG, "unlike clicked");
                                unlike(position);
                            } else {
                                Log.e(TAG, "like clicked");
                                like(position);
                            }
                            break;
                        case R.id.iv_share:
                            MyApplication.tracker().send(new HitBuilders.EventBuilder()
                                    .setCategory("ShouYe")
                                    .setAction(mp4FullPath+":"+"FenXiangButton")
                                    .build());
                            shareVideo(position);
                            break;
                    }
                } else {
                    Toast.makeText(context, getResources().getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 检测视频是否已下载
     *
     * @param mp4Name
     * @return
     */
    private Boolean checkIsDownLoad(String mp4Name) {
        String downPath = Tool.getDownloadDir(context);
        String filePath = downPath + "/" + mp4Name;
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void registerDownloadReceiver() {
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_START);
        filter.addAction(DownloadService.ACTION_FINISH);
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMainListAdapter == null) {
                return;
            }
            if (DownloadService.ACTION_START.equals(intent.getAction())) {
                Log.e(TAG, "download start");
                long finished = intent.getLongExtra("finished", 0);
                String id = intent.getStringExtra("id");
                long length = intent.getLongExtra("downloadedLength", 0);
                String current = Formatter.formatFileSize(context, (Long) finished * length / 100);
                mMainListAdapter.updateProgress(id, finished, length);
            } else if (DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                Log.e(TAG, "download finish");
                String id = intent.getStringExtra("id");

                mMainListAdapter.updateProgress(id, 100, -1);
                //遍历集合，查找下载了第几个视频，改变图标为播放状态
                int position = -1;
                String videoName = intent.getStringExtra("videoName");
                for (int i = 0; i < listData.getVideoList().size(); i++) {
                    VideoInfo vi = listData.getVideoList().get(i);
                    if (vi.getFilepath().contains(videoName)) {
                        position = i;
                        Log.e("yjg", "thumb:" + videoName + "  position:" + i);
                    }
                }
                if (position != -1) {
                    listData.getVideoList().get(position).setDownloadStatus(2);
                    String oldPosition = PreferencesUtil.getWhichDownloaded(context);
                    PreferencesUtil.setWhichDownloaded(context, oldPosition + position);
                }
                //如果已下载，播放
                Intent intentVideo = new Intent(context, VideoPlayActivity.class);
                intentVideo.putExtra("videoUrl", Tool.getDownloadDir(context) + videoName);
                startActivity(intentVideo);
            }
        }
    };

    /**
     * 喜欢某个视频
     *
     * @param position
     */
    private void like(final int position) {
        int dataPosition = position -1;
        View itemView = recyclerView_mainlist.getLayoutManager().findViewByPosition(position);
        final MainListAdapter.MyViewHolder holder = (MainListAdapter.MyViewHolder) itemView.getTag();
        holder.ivLike.setSelected(true);
        listData.getVideoList().get(dataPosition).setLike(true);

        String oldLikePosition = PreferencesUtil.getWhichLike(context);
        PreferencesUtil.setWhichLike(context,oldLikePosition+dataPosition);
    }

    /**
     * 不喜欢某个视频
     *
     * @param position
     */
    private void unlike(final int position) {
        int dataPosition = position -1;
        View itemView = recyclerView_mainlist.getLayoutManager().findViewByPosition(position);
        final MainListAdapter.MyViewHolder holder = (MainListAdapter.MyViewHolder) itemView.getTag();
        holder.ivLike.setSelected(false);
        listData.getVideoList().get(dataPosition).setLike(false);

        //不喜欢从sp中替换掉position
        String oldLikePosition = PreferencesUtil.getWhichLike(context);
        String newLikePosition = oldLikePosition.replace(dataPosition+"*","");
        PreferencesUtil.setWhichLike(context,newLikePosition);
    }

    /**
     * 一键分享到whatsapp
     *
     * @param position
     */
    private void shareWhatsApp(final int position) {
        View itemView = recyclerView_mainlist.getLayoutManager().findViewByPosition(position);
        final MainListAdapter.MyViewHolder holder = (MainListAdapter.MyViewHolder) itemView.getTag();

        //添加分享的代码
        String fileName = listData.getVideoList().get(position - 1).getFilepath().substring(listData.getVideoList().get(position - 1).getFilepath().lastIndexOf('/'), listData.getVideoList().get(position - 1).getFilepath().length());
        String pathName = Tool.getDefaultDownloadDir() + fileName;
        Log.e(TAG, "path =" + pathName);
        isWhatsapp = true;
        shareVideoToWhatsapp(context, null, pathName);
    }

    /**
     * 分享视频到whatsapp
     *
     * @param context
     * @param subject
     * @param path
     */
    public void shareVideoToWhatsapp(Context context, String subject, String path) {
        File sendFile = new File(path);
        if (!sendFile.exists()) {
            Toast.makeText(context, getString(R.string.play_before_share), Toast.LENGTH_SHORT).show();
        } else {
            if (checkApkExist(context, "com.whatsapp")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setPackage("com.whatsapp");
                intent.setType("video/*");
                Uri VideoUri = Uri.fromFile(new File(path));
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_STREAM, VideoUri);
                context.startActivity(Intent.createChooser(intent, "Send"));

                //点击分享，穿越次数+1
                int useNum = PreferencesUtil.getChuanYueNum(context);
                Log.e(TAG, "useNum" + useNum);
                PreferencesUtil.setChuanYueNum(context, useNum + 1);
                Log.e(TAG, "Chuanyue" + (useNum + 1));
            } else {
                Toast.makeText(context, getString(R.string.no_find_whatsapp), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 分享视频到whatsapp
     *
     * @param context
     * @param subject
     * @param path
     */
    public void shareVideoToWhatsapps(Context context, String subject, String path) {
        File sendFile = new File(path);
        if (!sendFile.exists()) {
            Toast.makeText(context, getString(R.string.play_before_share), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri VideoUri = Uri.fromFile(new File(path));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_STREAM, VideoUri);
            context.startActivity(Intent.createChooser(intent, "Send"));
        }
    }

    /**
     * 判断是否安装WhatsApp
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info != null) {
                return true;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void shareVideo(int position) {
        View itemView = recyclerView_mainlist.getLayoutManager().findViewByPosition(position);
        final MainListAdapter.MyViewHolder holder = (MainListAdapter.MyViewHolder) itemView.getTag();

        //点击分享，穿越次数+1
        int useNum = PreferencesUtil.getChuanYueNum(context);
        Log.e(TAG, "useNum" + useNum);
        PreferencesUtil.setChuanYueNum(context, useNum + 1);
        Log.e(TAG, "Chuanyue" + (useNum + 1));

        String fileName = listData.getVideoList().get(position - 1).getFilepath().substring(listData.getVideoList().get(position - 1).getFilepath().lastIndexOf('/'), listData.getVideoList().get(position - 1).getFilepath().length());
        String pathName = Tool.getDefaultDownloadDir() + fileName;
        shareVideoToWhatsapps(context, null, pathName);
    }
}

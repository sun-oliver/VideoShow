package com.helloworld.videoshow;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.helloworld.videoshow.base.BaseActivity;
import com.helloworld.videoshow.fragment.VideoFragment;
import com.helloworld.videoshow.utils.DataUtils;
import com.helloworld.videoshow.utils.Tool;
import com.helloworld.videoshow.view.SmileBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    public static TextView mTvTravelNum;//穿越次数文本框
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    /*private static final int UPDATE = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    ((VideoFragment)mFragments.get(0)).mMainListAdapter.removeAll();
                    ((VideoFragment)mFragments.get(0)).mMainListAdapter.add(((VideoFragment)mFragments.get(0)).listData.getVideoList());
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setToolbarTitle(toolbar, getString(R.string.app_name));

        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
        requestPermission();
        initView();

        /*String initLastUseDay = PreferencesUtil.getLastUseDay(MainActivity.this);//初始化第一次使用时间
        Log.e(TAG,"initLastUseDay:" + initLastUseDay);
        currentTime = Tool.getCurrentTime();
        //第一次进入，初始化默认穿越次数为3；刷新不改变穿越次数
        if (Integer.parseInt(currentTime) - Integer.parseInt(initLastUseDay) > 0) {
            PreferencesUtil.setLastUseDay(MainActivity.this, currentTime);//将当前时间保存到sp
            Log.e(TAG, "初始化穿越次数");
            //设置穿越次数，默认次数为3
            PreferencesUtil.setChuanYueNum(this, 3);
        }else {
            //当天第二次打开，调用SharedPreferences给穿越次数文本框赋值
            int useNum = PreferencesUtil.getChuanYueNum(this);
            Log.e(TAG, "useNum" + useNum);
            mTvTravelNum.setText(useNum+"");
        }*/

        setSelect(0);
        initViewClickListener();
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view_left);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragments = new ArrayList<Fragment>();
        Fragment mTab01 = new VideoFragment();
        mFragments.add(mTab01);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner_title1:
                setSelect(DataUtils.BANNER_FIRST);
                break;
            case R.id.nav_share:
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("CeBianLan")
                        .setAction("ShareApp")
                        .build());
                Tool.share(MainActivity.this);
                break;
            case R.id.nav_comment:
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("CeBianLan")
                        .setAction("PingFen")
                        .build());
                showCommentDialog();
                break;
            case R.id.nav_feedback:
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("CeBianLan")
                        .setAction("FanKui")
                        .build());
                Tool.feedback(MainActivity.this);
                break;
            case R.id.nav_about:
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("CeBianLan")
                        .setAction("GuanYu")
                        .build());
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    /**
     * 标题栏点击不同的标题时，ViewPager切换到对应的页
     *
     * @param i
     */
    private void setSelect(int i) {
        mViewPager.setCurrentItem(i);
    }

    /**
     * 导航菜单
     */
    private void initViewClickListener() {
        //left nav
        findViewById(R.id.nav_share).setOnClickListener(this);
        findViewById(R.id.nav_comment).setOnClickListener(this);
        findViewById(R.id.nav_feedback).setOnClickListener(this);
        findViewById(R.id.nav_about).setOnClickListener(this);
    }

    /**
     * 评分对话框
     */
    AlertDialog mAlertDialog;
    private void showCommentDialog() {


        View commentLayout = LayoutInflater.from(this).inflate(R.layout.dialog_comments, null);
        final SmileBar smileBar = (SmileBar) commentLayout.findViewById(R.id.ratingBar);
        Button btnGoToCommnet = (Button) commentLayout.findViewById(R.id.btn_go_comment);
        final TextView tv = (TextView) commentLayout.findViewById(R.id.dialog_comment_tv);


        mAlertDialog = new AlertDialog.Builder(this).setView(commentLayout).create();
        mAlertDialog.show();
        smileBar.setOnRatingSliderChangeListener(new SmileBar.OnRatingSliderChangeListener() {
            @Override
            public void onPendingRating(int rating) {
                Log.e(TAG,"Rate us0");
            }

            @Override
            public void onFinalRating(int rating) {
                if (smileBar.getRating() == 5){
                    tv.setText(getString(R.string.rate_play));
                }else {
                    tv.setText(getString(R.string.four_rate_play));
                }
            }

            @Override
            public void onCancelRating() {
            }
        });

        btnGoToCommnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smileBar.getRating() == 5) {
                    Tool.rateOrReview(MainActivity.this, getPackageName());
                } else {
                    Tool.feedback(MainActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    /**
     * 请求权限
     */
    private static final int REQUEST_PERMISSION_STORAGE_CODE = 1;
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("app需要开启权限才能使用此功能")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
                }else {
                    //没有权限，申请权限
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_STORAGE_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"测试：同意授权",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"测试：不同意授权",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
}

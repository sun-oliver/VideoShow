package com.helloworld.videoshow;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.helloworld.videoshow.utils.Tool;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "SplashActivity";
    private List<View> mViews = new ArrayList<View>();
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private ImageView[] dots;//splash页小圆点图片
    private int currentIndex = 0;//viewpager当前页索引

    private LinearLayout ll_dots;
    private Button btnSkip;//跳过按钮
    private ImageButton imgBtnNext;//下一步按钮
    private Button btnFinish;//完成按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        initDots();
        initListener();
        updateDots(0);
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.splash_viewpager);
        //将三个xml转换成view
        LayoutInflater inflater = LayoutInflater.from(this);
        mViews.add(inflater.inflate(R.layout.layout_splash1, null));
        mViews.add(inflater.inflate(R.layout.layout_splash2, null));
        mViews.add(inflater.inflate(R.layout.layout_splash3, null));

        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            /**
             * 销毁viewpager中超过3个的pager
             */
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViews.get(position));
                //super.destroyItem(container, position, object);
            }

            //初始化item
            //container就是viewpager本身
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }
        };
        mViewPager.setAdapter(mAdapter);

        ll_dots = (LinearLayout) findViewById(R.id.splash_ll_points_container);
        btnSkip = (Button) findViewById(R.id.splash_btn_skip);
        imgBtnNext = (ImageButton) findViewById(R.id.splash_imagebutton_next);
        btnFinish = (Button) findViewById(R.id.splash_btn_finish);
    }

    /**
     * 初始化splash页的点
     */
    private void initDots() {
        int circleWidthPx = 20;
        int circleSpacePx = 8;
        int widthScreenPx = this.getResources().getDisplayMetrics().widthPixels;
        //0.25是skip的宽度，0.5中间的宽度减去三个圆点的宽度，减去两个间隙
        int paddintLeftPx = (int)(0.25 * widthScreenPx + (0.5 * widthScreenPx - circleWidthPx * mViews.size()- circleSpacePx * (mViews.size()-1))/2);
        int paddingLeftDp = Tool.px2dp(this,paddintLeftPx);
        for (int i = 0; i < mViews.size(); i++) {
            View view = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(circleWidthPx, circleWidthPx);
            lp.gravity = Gravity.CENTER;
            lp.leftMargin = paddingLeftDp;
            if (i != 0) {
                lp.leftMargin = circleSpacePx;
            }
            view.setLayoutParams(lp);
            view.setBackgroundResource(R.drawable.selector_dot);
            ll_dots.addView(view);
        }
    }

    private void initListener() {
        btnSkip.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        imgBtnNext.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                Log.e(TAG,"onPageSelected"+"   position:"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void updateDots(int position){
        if(position < 2 ){
            imgBtnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }else{
            imgBtnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        }
        for(int i = 0 ; i< ll_dots.getChildCount();i++) {
            ll_dots.getChildAt(i).setEnabled(i == position);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.splash_btn_skip:
            case R.id.splash_btn_finish:
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
                break;
            case R.id.splash_imagebutton_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
        }
    }
}

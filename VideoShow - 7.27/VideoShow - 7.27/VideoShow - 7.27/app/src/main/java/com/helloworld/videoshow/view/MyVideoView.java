package com.helloworld.videoshow.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.helloworld.videoshow.utils.Tool;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sing on 2016/5/30.
 */
public class MyVideoView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = "MyVideoView";

    public static final int POSITION_UPDATE_NOTIFYING_PERIOD = 500;

    // all possible internal states
    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private int         mVideoWidth;
    private int         mVideoHeight;

    private ScheduledExecutorService mPositionUpdateNotifier = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> mFuture;

    private Uri mUri;
    private int         mCurrentBufferPercentage;
    private boolean isLooping = false;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState  = STATE_IDLE;
    private int         mSeekWhenPrepared;

    private float volume=0.5f;

    private SurfaceTexture mSurfaceTexture;
    private OnPlayerListener mOnPlayerListener;

    public MyVideoView(Context context) {
        this(context, null, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setSurfaceTextureListener(this);
        initVideoView();
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState  = STATE_IDLE;
    }

    public void setVideoUri(Uri uri) {
        mUri = uri;
        openVideo();
    }

    public void setVolume(float mVolume) {
        volume = mVolume;
        mMediaPlayer.setVolume(volume,volume);
    }

    public void setOnPlayerListener(OnPlayerListener listener) {
        mOnPlayerListener = listener;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }


    private void openVideo() {
        if (mUri == null || mSurfaceTexture == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        //设置最大音量
        //am.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume),AudioManager.FLAG_PLAY_SOUND);

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            Surface surface = new Surface(mSurfaceTexture);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setLooping(isLooping);

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalStateException e) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            mCurrentState = STATE_PREPARED;
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onVideoSizeChanged(mVideoWidth,mVideoHeight);
                mOnPlayerListener.onPrepared();
            }
            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // We didn't actually change the size (it was already at the size
                // we need), so we won't get a "surface changed" callback, so
                // start the video here instead of in the callback.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                requestLayout();
            }
        }
    };

    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {

        public  boolean onInfo(MediaPlayer mp, int arg1, int arg2) {
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onInfo(arg1, arg2);
            }
            return true;
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {

        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onError(framework_err, impl_err);
                return true;
            }
            return false;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if(mOnPlayerListener != null) {
                mOnPlayerListener.onVideoCompletion();
            }
        }
    };

    public boolean isPlaying() {
        //return isInPlaybackState() && mMediaPlayer.isPlaying();
        return mMediaPlayer.isPlaying();
    }

    public boolean isVoice(){
        return volume == 0 ? false:true;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }


    public void stopPlayback() {
        if (mMediaPlayer != null) {
            stopPositionUpdateNotifier();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState  = STATE_IDLE;
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            startPositionUpdateNotifier();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    private void startPositionUpdateNotifier() {
        mFuture = mPositionUpdateNotifier.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        if(mOnPlayerListener != null && mMediaPlayer.isPlaying()) {
                            mOnPlayerListener.onProgress(mMediaPlayer.getCurrentPosition());
                        }
                    }
                },
                0, POSITION_UPDATE_NOTIFYING_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void stopPositionUpdateNotifier() {
        if(mFuture != null) {
            mFuture.cancel(true);
            mFuture = null;
        }

    }

    /*
     * TextureView.SurfaceTextureListener
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurfaceTexture = surfaceTexture;
        if(isInPlaybackState()) {
            Surface surface = new Surface(mSurfaceTexture);
            mMediaPlayer.setSurface(surface);
            start();
        } else {
            openVideo();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceTexture = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    public synchronized void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            stopPositionUpdateNotifier();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState  = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    public interface OnPlayerListener {

        void onVideoSizeChanged(int width, int height);

        void onPrepared();

        void onProgress(int progress);

        void onVideoCompletion();

        void onError(int what, int error);

        void onInfo(int what, int extra);
    }



}


package zunvideodemo.wangyang.www.zun1videodemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import zunvideodemo.wangyang.www.zun1videodemo.R;


/**
 * Created by pc on 2015/12/7.
 */

public class VideoViewLayout extends LinearLayout implements MediaController.MediaPlayerControl,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {

    //Vitamio MediaPlayer
    private MediaPlayer mediaPlayer;
    private Uri mUri;

    private boolean isPrepared = false;
    private boolean isVideoSizeKnow = false;
    //视频的宽、高
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    //视频播放的SurfaceView
    public TextureView surfaceView;
    private Surface surface;


    public VideoViewLayout(Context context) {
        this(context, null);
    }

    public VideoViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        afterView();
    }

    public void afterView() {
        inflate(getContext(), R.layout.video_view_layout, this);
        surfaceView = (TextureView) findViewById(R.id.texture_view);
        surfaceView.setSurfaceTextureListener(new MyTextureListener());
        //设置声音的模式
        if (getContext() instanceof Activity)
            ((Activity) getContext()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置视频的播放路径，同时会自动开始播放，不需要再次调用{@link #start()}
     *
     * @param path 视频路径http/rtsp等
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        openVideo();
        //刷新界面
        requestLayout();
        invalidate();
    }


    private void openVideo() {
        if (mUri == null || null == surface || !Vitamio.isInitialized(getContext()))
            return;

        pauseMusic();
        release();

        try {
            if (null == mediaPlayer)
                mediaPlayer = new MediaPlayer(getContext(), true);
            mediaPlayer.setDataSource(getContext(), mUri);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //释放资源
    public void release() {
        if (null == mediaPlayer)
            return;
        mediaPlayer.setOnVideoSizeChangedListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnBufferingUpdateListener(null);
        mediaPlayer.setOnInfoListener(null);
        mediaPlayer.setSurface(null);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        isVideoSizeKnow = false;
        isPrepared = false;
    }

    //暂停其他的播放
    private void pauseMusic() {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        getContext().sendBroadcast(i);
    }


    private void startVideoPlayback() {
        if (mVideoHeight == 0 || mVideoWidth == 0)
            return;
        mediaPlayer.start();
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = surfaceView.getWidth();
        int viewHeight = surfaceView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;

        Matrix txform = new Matrix();
        surfaceView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        surfaceView.setTransform(txform);
    }


    @Override
    public void start() {
        if (isPrepared && isVideoSizeKnow)
            startVideoPlayback();
    }

    @Override
    public void pause() {
        if (mediaPlayer == null)
            return;
        mediaPlayer.pause();
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(long pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        if (isVideoSizeKnow) {
            startVideoPlayback();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        adjustAspectRatio(width, height);
        isVideoSizeKnow = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (isPrepared) {
            startVideoPlayback();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK: // line added 1
                long buffersize = mp.audioTrackInit(); // line added 2
                mp.audioInitedOk(buffersize); // line added 3
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //
        if (mp != null) {
            mp.stop();
            mp.reset();
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //view和屏幕分离时，释放资源
        release();
    }

    private class MyTextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            surface = new Surface(surfaceTexture);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

}

package zunvideodemo.wangyang.www.zun1videodemo.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.vov.vitamio.MediaPlayer;
import zunvideodemo.wangyang.www.zun1videodemo.R;

/**
 * Created by pc on 2015/12/8.
 */
public class MediaControllerView extends LinearLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private ImageButton ibtPlayOrPause;
    private AppCompatSeekBar seekBarVideo;
    private TextView tvTime;

    //这里采用弱引用，防止MediaPlayer无法回收
    private WeakReference<MediaPlayer> mMediaPlayer;

    public MediaControllerView(Context context) {
        this(context, null);
    }

    public MediaControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        inflate(getContext(), R.layout.media_controller_layout, this);
        ibtPlayOrPause = (ImageButton) findViewById(R.id.ibt_play_pause);
        seekBarVideo = (AppCompatSeekBar) findViewById(R.id.seek_media_player);
        tvTime = (TextView) findViewById(R.id.tv_time);

        addListener();
    }


    private void addListener() {
        ibtPlayOrPause.setOnClickListener(this);
        seekBarVideo.setOnSeekBarChangeListener(this);
    }

    //注入MediaPlayer
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        if (mMediaPlayer == null && mediaPlayer != null)
            mMediaPlayer = new WeakReference<MediaPlayer>(mediaPlayer);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

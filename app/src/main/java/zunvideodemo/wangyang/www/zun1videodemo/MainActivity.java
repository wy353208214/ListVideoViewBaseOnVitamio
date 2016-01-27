package zunvideodemo.wangyang.www.zun1videodemo;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.Vitamio;
import zunvideodemo.wangyang.www.zun1videodemo.video.VideoBean;
import zunvideodemo.wangyang.www.zun1videodemo.view.VideoViewLayout;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    @ViewById(R.id.text)
    Button button;

    @ViewById(R.id.listView)
    ListView listView;

    VideoAdapter videoAdapter;
    private List<VideoBean> videoBeans = new ArrayList<VideoBean>();

    private String videos[] = {"http://192.168.1.147/hls/a1/tp1.m3u8",
            "http://119.147.135.245/edge.v.iask.com/139547981.hlv?KID=sina,viask&Expires=1453910400&ssig=xpdIbO1YfE&sid=250398430_2395099704_1453780236123_1087_4198724&corp=1",
            "http://119.147.135.245/edge.v.iask.com/139694147.hlv?KID=sina,viask&Expires=1453910400&ssig=%2BgTNX4pzSe&sid=250454455_2395099704_1453780337796_251_8318975&corp=1",
            "http://14.17.79.253/edge.v.iask.com/139655939.hlv?KID=sina,viask&Expires=1453910400&ssig=Qkld9T%2BVs0&sid=250439729_2395099704_1453780354218_306_8378870&corp=1",
            "http://vodfile3.news.cn/data/cdn_transfer/57/59/5747b72f859230ea3f79b7019065948027d9ad59.mp4"
    };

    @AfterViews
    public void afterView() {
        Vitamio.isInitialized(getApplicationContext());
        for (int i = 0; i < videos.length; i++) {
            VideoBean videoBean = new VideoBean();
            videoBean.setUrl(videos[i]);
            videoBeans.add(videoBean);
        }

        videoAdapter = new VideoAdapter(videoBeans, getApplicationContext());
        listView.setAdapter(videoAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
    }

    @Click(R.id.text)
    public void btnClick() {
//        VideoPlayActivity_.intent(this).start();
//        startActivity(new Intent(this, MediaPlayerActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //设置正在播放的视频的position，并刷新界面
        if (videoAdapter.getPlayingViewHolder() != null) {
            VideoViewLayout videoView = videoAdapter.getPlayingViewHolder().videoView;
            videoView.release();
        }

        videoAdapter.setPlayPosition(position);
        videoAdapter.notifyDataSetChanged();
        VideoBean videoBean = (VideoBean) videoAdapter.getItem(position);
        VideoAdapter.ViewHolder viewHolder = (VideoAdapter.ViewHolder) view.getTag();
        viewHolder.videoView.setVideoPath(videoBean.getUrl());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        VideoAdapter.ViewHolder playViewHolder = videoAdapter.getPlayingViewHolder();
        int playPosition = videoAdapter.getPlayPosition();
        if (playPosition == -1 || playViewHolder == null)
            return;

        int lastVisibleItem = firstVisibleItem + visibleItemCount;

        //1.如果顶部第一条可见的Item位置小于正在播放的position，或者底部最后一条可见的item大于position，就暂停播放
        if (firstVisibleItem > playPosition || lastVisibleItem < playPosition)
            playViewHolder.videoView.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoAdapter.ViewHolder playViewHolder = videoAdapter.getPlayingViewHolder();
        if (playViewHolder != null) {
            playViewHolder.videoView.release();
        }
    }
}

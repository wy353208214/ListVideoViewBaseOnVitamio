package zunvideodemo.wangyang.www.zun1videodemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import zunvideodemo.wangyang.www.zun1videodemo.video.VideoBean;
import zunvideodemo.wangyang.www.zun1videodemo.view.VideoViewLayout;


/**
 * Created by pc on 2015/12/2.
 */
public class VideoAdapter extends BaseAdapter {

    private List<VideoBean> datas;
    private Context context;

    //记录播放视频的item位置
    private int playPosition = -1;

    public VideoAdapter(List<VideoBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_video, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.videoView = (VideoViewLayout) convertView.findViewById(R.id.video_view);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.images);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //判断正常播放的视频的位置，并设置是否显示videoview
        if (position == playPosition) {
            playingViewHolder = viewHolder;
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
        } else {
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        VideoViewLayout videoView;
        ImageView imageView;
    }


    /**
     * 记录正在播放的ViewHolder
     */
    private ViewHolder playingViewHolder;

    public ViewHolder getPlayingViewHolder() {
        return playingViewHolder;
    }

    public void setPlayingViewHolder(ViewHolder playingViewHolder) {
        this.playingViewHolder = playingViewHolder;
    }

}

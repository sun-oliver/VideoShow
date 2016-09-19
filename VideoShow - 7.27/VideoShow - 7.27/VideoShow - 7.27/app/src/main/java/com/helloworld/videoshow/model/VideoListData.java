package com.helloworld.videoshow.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hello on 2016/7/12.
 */
public class VideoListData implements Serializable {

    private int code;
    private List<VideoInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<VideoInfo> getVideoList(){
        return data;
    }

    public void setVideoList(List<VideoInfo> list){
        this.data = data;
    }

    @Override
    public String toString() {
        return "VideoListData{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}

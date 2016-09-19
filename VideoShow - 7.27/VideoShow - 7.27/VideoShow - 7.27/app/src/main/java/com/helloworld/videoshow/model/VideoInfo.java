package com.helloworld.videoshow.model;

import java.io.Serializable;

/**
 * Created by Hello on 2016/7/7.
 * 视频对象
 */
public class VideoInfo implements Serializable {
    private String uuid;
    private String description;//视频描述，标题
    private String thumb;//视频缩略图网址
    private String filepath;//视频网址
    private String duration;//视频时长，单位s
    private long size;//视频总长度
    private long createtime;//视频创建时间
    private long finished;//视频已下载长度

    private Boolean like = false;//点赞
    private int whatsApp;//whatsApp分享

    private int downloadStatus=0;//视频的状态，0未下载；1开始下载；2完成下载

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
    @Override
    public String toString() {
        return "VideoInfo{" +
                "uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", thumb='" + thumb + '\'' +
                ", filepath='" + filepath + '\'' +
                ", duration='" + duration + '\'' +
                ", size=" + size +
                ", createtime=" + createtime +
                ", finished=" + finished +
                '}';
    }
}

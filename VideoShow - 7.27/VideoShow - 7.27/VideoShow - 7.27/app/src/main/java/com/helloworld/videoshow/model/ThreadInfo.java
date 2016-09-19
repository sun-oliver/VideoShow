package com.helloworld.videoshow.model;

/**
 * Created by Du on 2015/11/24.
 */
public class ThreadInfo {
    private int id;
    private String url;
    private long start;
    private long end;
    private long finished;

    public ThreadInfo() {
    }

    public ThreadInfo(long end, long finished, int id, long start, String url) {
        this.end = end;
        this.finished = finished;
        this.id = id;
        this.start = start;
        this.url = url;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "end=" + end +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", finished=" + finished +
                '}';
    }
}

package com.helloworld.videoshow.database;

import com.helloworld.videoshow.model.ThreadInfo;

import java.util.List;

/**
 * Created by Du on 2015/11/24.
 */
public interface ThreadDAO {
    void insertThread(ThreadInfo threadInfo);
    void deleteThread(String url);
    void deleteAllThreads();
    void updateThread(String url, int thread_id, long finished);
    List<ThreadInfo> getThreads(String url);
    boolean isExists(String url);
}

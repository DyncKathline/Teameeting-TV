package org.dync.tv.teameeting.chatmessage;


import org.dync.tv.teameeting.bean.ReqSndMsgEntity;

/**
 * Created by zhulang on 2016/1/9 0009.
 */
public interface IChatMessageInteface {
    void onRequesageMsg(ReqSndMsgEntity requestMsg);
}

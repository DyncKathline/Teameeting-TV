package org.dync.tv.teameeting.bean;

/**
 * Created by zhulang on 2015/12/28 0028.
 */
public class SelfData {

    private String authorization;
    private int code;
    private InformationEntity information;
    private String message;
    private long requestid;
    private boolean mIsNetConnected;

    
    public boolean ismIsNetConnected() {
        return mIsNetConnected;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setInformation(InformationEntity information) {
        this.information = information;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }

    public String getAuthorization() {
        return authorization;
    }

    public int getCode() {
        return code;
    }

    public InformationEntity getInformation() {
        return information;
    }

    public String getMessage() {
        return message;
    }

    public long getRequestid() {
        return requestid;
    }

    public void setmIsNetConnected(boolean mIsNetConnected) {
        this.mIsNetConnected = mIsNetConnected;
    }


    public class InformationEntity {
        private int uactype;
        private int ulogindev;
        private String uname;
        private String upushtoken;
        private long uregtime;
        private int uregtype;
        private String userid;
        private int ustatus;

        public void setUactype(int uactype) {
            this.uactype = uactype;
        }

        public void setUlogindev(int ulogindev) {
            this.ulogindev = ulogindev;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public void setUpushtoken(String upushtoken) {
            this.upushtoken = upushtoken;
        }

        public void setUregtime(long uregtime) {
            this.uregtime = uregtime;
        }

        public void setUregtype(int uregtype) {
            this.uregtype = uregtype;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public void setUstatus(int ustatus) {
            this.ustatus = ustatus;
        }

        public int getUactype() {
            return uactype;
        }

        public int getUlogindev() {
            return ulogindev;
        }

        public String getUname() {
            return uname;
        }

        public String getUpushtoken() {
            return upushtoken;
        }

        public long getUregtime() {
            return uregtime;
        }

        public int getUregtype() {
            return uregtype;
        }

        public String getUserid() {
            return userid;
        }

        public int getUstatus() {
            return ustatus;
        }

        @Override
        public String toString() {
            return "InformationEntity{" +
                    "uactype=" + uactype +
                    ", ulogindev=" + ulogindev +
                    ", uname='" + uname + '\'' +
                    ", upushtoken='" + upushtoken + '\'' +
                    ", uregtime=" + uregtime +
                    ", uregtype=" + uregtype +
                    ", userid='" + userid + '\'' +
                    ", ustatus=" + ustatus +
                    '}';
        }
    }
}

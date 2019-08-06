package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

/**
 * 响应消息
 * Created by sam on 2019/8/4.
 */
public class ChromeAppResponseMessage {

    private boolean success;
    private String msg;

    public ChromeAppResponseMessage(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}

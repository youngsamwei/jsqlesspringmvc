package cn.sdkd.ccse.jsqles.chromeapp.msgprocessor;

/**
 * Created by sam on 2019/8/4.
 */
public class ChromeAppMsgProcessor {
    private ChromeAppRequestMessage caMsg;

    public ChromeAppMsgProcessor(ChromeAppRequestMessage msg){
        this.caMsg = msg;
    }
    public ChromeAppMsgProcessor(String msg){
        ChromeAppRequestMessage chromeAppRequestMessage = new ChromeAppRequestMessage();
        /*处理msg，转换为chromeAppRequestMessage*/
        this.caMsg = chromeAppRequestMessage;
    }
    public ChromeAppResponseMessage process(){
        ChromeAppResponseMessage chromeAppResponseMessage  = new ChromeAppResponseMessage();

        /*根据消息请求做处理，将反馈结果封装为chromeAppResponseMessage*/

        return chromeAppResponseMessage;
    }
}

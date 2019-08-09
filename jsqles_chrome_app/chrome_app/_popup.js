
function updateResult(obj, state){
    var tmp = document.createElement('p');
    tmp.innerHTML = state;

    document.getElementById(obj).appendChild(tmp);

    //	document.getElementById(obj).innerHTML = state;
}

function invoke(){
	var hostName = "cn.sdkd.ccse.jsqles.chromeapp.host.chromeapphost";
	var port = chrome.runtime.connectNative(hostName);
	port.onMessage.addListener(function(msg) {//收到消息后的处理函数
	    updateResult("result1", "Received" + JSON.stringify(msg));
    });
    port.onDisconnect.addListener(function() {
        updateResult("result1", "Disconnected" );
    });

//    port.postMessage({"message": "select @@version" });
    port.postMessage({ message: "go" });
}

function listener(){
	updateResult("result2", "listen");
}

document.addEventListener('DOMContentLoaded', function() {
  document.querySelector('#button1').addEventListener(
      'click', invoke);
});
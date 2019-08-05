		window.onload=function(){
			//after html loaded
			var obj = document.getElementById('response_div_id');
			obj.addEventListener('customEvent', function(e){
				console.log('customEvent 事件触发了=>'+e.detail.data);
			}, false);
		}

		  // The ID of the extension we want to talk to.
			var kagulaExtensionId = "oeejofojochggegmkbmjbjhiojakbcme";

			function requestConnect()
			{
				console.log("connect");
				chrome.runtime.sendMessage(kagulaExtensionId, {data: "connect"},
                      function(response) {
                        console.log(response);
                });
			}

			function requestEcho()
			{
				console.log("Make a simple request");
				var reqData =   document.getElementById('input-text').value;
				chrome.runtime.sendMessage(kagulaExtensionId, {data: reqData},
				  function(response) {
				  	console.log("response: " + JSON.stringify(response));
				});
			}

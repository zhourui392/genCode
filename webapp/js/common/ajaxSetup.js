$.ajaxSetup({
	contentType : "application/x-www-form-urlencoded;charset=utf-8",
	cache:false ,
	error : function(XMLHttpRequest, textStatus,errorThrown) {
		var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus"); //通过XMLHttpRequest取得响应头，sessionstatus，  
		if (sessionstatus == "isdelete") {
 			alert(i18nKey("system.sessionInvalid"));
 			document.location.href="../../login.html";
		}else if (sessionstatus == "timeout") {
 			alert(i18nKey("menu.sessionTimeOut"));
 			//如果超时就处理 ，指定要跳转的页面  
 			document.location.href="../../login.html";
		}else if(sessionstatus=="no-permission"){
			alert(i18nKey("system.noAccessRight"));
 			//如果超时就处理 ，指定要跳转的页面  
 			document.location.href="../../login.html";
		}
	},
	success: function (data) {
		if (data.status == 1 && data.data == "会话已失效,请重新登录!"){
			alert(i18nKey("system.sessionInvalid"));
			document.location.href="../../login.html";
		}
	},
	complete:function(data,TS){
		//if (data.status == 1 && data.data == "会话已失效,请重新登录!"){
		//	alert("会话已失效,请重新登录!");
		//	document.location.href="../../login.html";
		//}
	}
});
softtype = 1;
projectName = "";
$(function(){
	var curWwwPath=window.document.location.href;
    var pathName=window.document.location.pathname;
    projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	loadSoftType();
});
function loadSoftType(){
	$.ajax({
		 url: projectName+"/quality/getsofttype",
         dataType: "json",
         type: "GET",
		 async:false,
         success: function (data) {
        	 if (data.status == 1){
        		 softtype = data.data;
        	 }
         }
	});
}

function isCloudSoft(){
	if (softtype == 0) return true;
}

function isHospitalSoft(){
	if (softtype == 1) return true;
}

var TusAjax = (function(){
	this.default_timeout = 60;
	this.async = false;
	var defalutError = function(event, XMLHttpRequest, ajaxOptions, thrownError){

	}

	var defalutSuccess = function(xhr,options){

	}

	return{
		post : function(url,data,success, error,timeout,async) {
			if (async == null){
				async = this.async;
			}
			if (timeout == null){
				timeout = this.default_timeout;
			}
			$.ajax({
				url: url,
				type: "POST",
				dataType: "json",
				data:data,
				async: async,
				timeout : timeout,
				error : function(event, XMLHttpRequest, ajaxOptions, thrownError){
					defalutError(event, XMLHttpRequest, ajaxOptions, thrownError);
					if (error != null){
						error();
					}
				},
				success: function (data,xhr,options) {
					defalutSuccess(xhr,options);
					if (success != null){
						success(data);
					}
				}
			});
		},

		get : function(url,data,success,error,timeout,async) {
			if (async == null){
				async = this.async;
			}
			if (timeout == null){
				timeout = this.default_timeout;
			}
			$.ajax({
				url: url,
				type: "GET",
				dataType: "json",
				data:data,
				async: async,
				timeout : timeout,
				error : function(event, XMLHttpRequest, ajaxOptions, thrownError){
					defalutError(event, XMLHttpRequest, ajaxOptions, thrownError);
					if (error != null){
						error();
					}
				},
				success: function (data,xhr,options) {
					defalutSuccess(xhr,options);
					if (success != null){
						success(data);
					}
				}
			});
		},
		setDefalutFunction : function(methods){
			if (methods.defalutError != null){
				this.defalutError = methods.defalutError;
			}
			if (methods.defalutSuccess!=null){
				this.defalutSuccess = methods.defalutError;
			}
		}
	}
})();

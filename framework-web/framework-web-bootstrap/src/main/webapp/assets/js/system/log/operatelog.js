/**
 * 
 */

//加载事件查询条件
$(function() {
	$.ajax({
		type : 'post',
		url : basePath + '/operatelog/qryEventList',
		success : function(data){
			var eventList = data.eventList;
			for(var i = 0;i < eventList.length;i++){
				$("#qryEventList").append("<li><a style='cursor: pointer;' onclick='searchLog("+eventList[i].eventId+");'>" + eventList[i].eventName + "</a></li>");
			}
		}
	});
});

function searchLog(index){
	$("#eventid").val(index);
	$("#operateLogForm").submit();
}

function checkOperateLog(obj){
	var tr = $(obj).parent().parent();
	var children = tr.children();
	var operateLogId = children.eq(0).text();
	var userName = children.eq(1).text();
	var createTime = children.eq(2).text();
	var eventName = children.eq(3).text();
	var ip = children.eq(4).text();
	
	var message = "<tr>" +
				  	"<th>操作日志标识：</th>" +
			      	"<td style='padding-right:165px;'>" 
				  		+ operateLogId + 
				  	"</td>" +
				  	"<th>操作员名称：</th>" +
				  	"<td>" 
				  		+ userName + 
				  	"</td>" +
				  "</tr>" +
				  "<tr style='line-height:30px;'>" +
				  "<th>创建时间：</th>" +
			      	"<td style='padding-right:165px;'>" 
				  		+ createTime + 
				  	"</td>" +
				  	"<th>事件名称：</th>" +
				  	"<td>" 
				  		+ eventName + 
				  	"</td>" +
				  "</tr>" +
				  "<tr style='line-height:30px;'>" +
				  "<th>IP地址：</th>" +
			      	"<td style='padding-right:165px;'>" 
				  		+ ip + 
				  	"</td>" +
				  "</tr>";
	
	bootbox.dialog({
		message: message,
		buttons: {
			danger: {
				label: "关闭"
				//className: "btn-danger"
			}
		}
	});
}

//导出
function operateLogExport(){
	window.location = basePath + "/operatelog/export?starttime=" + getParam("starttime") + "&endtime=" + getParam("endtime") + "&eventid=" + getParam("eventid");
}

var getParam = function(name){
    var search = document.location.search;
    var pattern = new RegExp("[?&]"+name+"\=([^&]+)", "g");
    var matcher = pattern.exec(search);
    var items = null;
    if(null != matcher){
    	try{
    		items = decodeURIComponent(decodeURIComponent(matcher[1]));
        }catch(e){
            try{
                items = decodeURIComponent(matcher[1]);
            }catch(e){
                items = matcher[1];
            }
        }
    }
    return items;
};
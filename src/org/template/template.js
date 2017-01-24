$(".trValueList").html("");

var colomsNum = 0;
$(function(){
    refreshTable();
    $(".glyphicon-search").click(function(){
        refreshTable();
    });

    $('#searchUserName').bind('keypress',function(event){
        if(event.keyCode == "13"){
            refreshTable();
        }
    });

    $(".btn-add-record").click(function(){
        $("#myModalLabel").text("添加");
##modalValueInit

        $('#Goto').modal('show');
        $(".btn-edit-info").unbind("click");
        $(".btn-edit-info").click(function(){
            saveUser(0);
        });
    });

});

function saveUser(id){
    var loginName = $("#loginName").val();
    var userName = $("#userName").val();
    var userPwd = $("#userPwd").val();
    var json = {
        userId : id,
        loginName : loginName,
        userName : userName,
        userPwd : userPwd
    };
    $.ajax({
        url: "../../addUser",
        dataType: "json",
        data: json,
        async:false,
        type: "POST",
        error : function(){
            alert(i18nKey("system.networkErrorAndTry"));
        },
        success: function (data) {
            $('#Goto').modal('hide');
            if (data.status == 1){
                alert(i18nKey('system.saveSucceed'));
            }else{
                alertServerMsg(data.msg);
            }
            refreshTable();
        }
    });
}

function initButton(){
    //修改
    $("a.btn-success").each(function(){
##modalValueInit
        $(this).unbind("click");
        $(this).click(function(){
            var userId = $(this).attr("value");
            $.ajax({
                url: "../../getUserById",
                data:{"id":userId},
                dataType: "json",
                type: "POST",
                success: function (data) {
                    var user = data.data;
                    $("#myModalLabel").text("修改");
                    $("#userId").val(user.id);
                    $("#loginName").val(user.loginName);
                    $("#userName").val(user.userName);
                    $("#userPwd").val(user.passwd);
                    $('#Goto').modal('show');
                    $(".btn-edit-info").unbind("click");
                    $(".btn-edit-info").click(function(){
                        saveUser(user.id);
                    });
                }
            });
        })
    });
    //删除
    $("a.btn-danger").each(function(){
        $(this).unbind("click");
        $(this).click(function(){
            if (confirm(i18nKey('system.deleteConfirm'))){
                var userId = $(this).attr("value");
                $.ajax({
                    url: "../../user/"+userId,
                    dataType: "json",
                    type: "DELETE",
                    success: function (data) {
                        if (data.status == 1){
                            var userHtml = "";
                            var userList = data.data;
                            if (userList == null || userList.length == 0){
                                userHtml = "<tr><td colspan='5' align='center'>无用户记录</td></tr>";
                            }
                            $(".trValueList").html(userHtml);
                            initButton();
                        }else{
                            $(".trValueList").html("<td>无用户信息</td>");
                        }
                        refreshTable();
                    }
                });
            }
        });
    });
}

function deleteUser(userId){
    if (confirm(i18nKey('system.deleteConfirm'))){
        $.ajax({
            url: "../../user/"+userId,
            dataType: "json",
            type: "DELETE",
            success: function (data) {
                refreshTable();
                alertServerMsg(data.msg);
            }
        });
    }
}

function refreshTable(){
    var searchUserName = $("#searchUserName").val();

    searchUserName = $.trim(searchUserName);

    var pageIndex = $("#pageIndex").val();
    if (pageIndex == null||pageIndex==""){
        pageIndex = 1;
    }
    var condition = "";
    if(searchUserName != null && searchUserName != "" ){
        condition = searchUserName;
    }
    var json = {
        searchUserName : condition,
        pageIndex : pageIndex
    };
    $.ajax({
        url: "../../users",
        dataType: "json",
        data:json,
        type: "GET",
        success: function (data) {
            if (data.status == 1){
                var userHtml = "";
                var userList = data.data.items;
                if (userList == null || userList.length == 0){
                    userHtml = "<tr><td colspan='4' align='center'>"+i18nKey("user.noItemList")+"</td></tr>";
                } else {
                    for (var index in userList){
                        var user = userList[index];
                        //var createDate = new Date(user.creationtime).format("yyyy-MM-dd hh:mm:ss");
                        userHtml += "<tr><td>"+user.showName+"</td>";
                        userHtml += "<td>"+user.username+"</td>";
                        userHtml += "<td>"+user.password+"</td>";
                        userHtml += "<td>"+user.status+"</td>";
                        userHtml += "<td><div class='oper'>";
                        userHtml += "<a href='javascript:void(0)' class='btn btn-success btn-xs' value="+user.id+"><span class='glyphicon glyphicon-pencil'></span></a>";
                        userHtml += "<a href='javascript:void(0)' class='btn btn-danger btn-xs' value="+user.id+" onclick='deleteUser("+user.id+",\""+user.hopitialId+"\")'><span class='glyphicon glyphicon-remove'></span></a>";
                        userHtml += "</tr>";
                    }
                }
                $(".trValueList").html(userHtml);
                $("#pageIndex").val(data.data.pageIndex);
                $("#pageToal").val(data.data.totalPage);
                $("#pageEachNum").val(data.data.limit);
            }else{
                $(".trValueList").html("<td colspan='5' align='center'>"+i18nKey("user.noItemList")+ "</td>");
            }
            initButton();
            //分页
            var page=$("#page");
            var options = {
                currentPage :  $("#pageIndex").val(),
                totalPages : $("#pageToal").val(),
                numberOfPages : 5,
                onPageClicked : function(event, originalEvent, type, page){
                    $("#page").hide();
                    $("#pageIndex").val(page);
                    $("#page").find("ul").addClass("pagination ").addClass("pagination-sm");
                    refreshTable();
                }
            }
            $("#page").bootstrapPaginator(options);
            $("#page").find("ul").addClass("pagination").addClass("pagination-sm");
            $("#page").show();
        },
        error:function(e){
            console.info("get user list error",e);
        }
    });
}
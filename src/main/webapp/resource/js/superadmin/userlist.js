$(function () {
    showUserListPage(null, null);
    $("#next-page").click(function() {
        showUserListPage($("#search").val(), $page + 1, true);
    })
    $("#pre-page").click(function() {
        showUserListPage($("#search").val(), $page - 1, true);
    })
    $("#search-button").click(function() {
        showUserListPage($("#search").val(), 1, true);
    })
    $("#dump-page-button").click(function() {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if(!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showUserListPage($("#search").val(), $("#dump-page-input").val(), true);
    })

    //点击开张或倒闭
    $(document).on("click", ".operation", function() {
       let userId = $(this).attr("user-id");
       let enableStatus = $(this).attr("enable-status");
       operationShop(userId, enableStatus);
    });

})

function operationShop(userId, enableStatus) {
    var confirmInfo = ""
    if (enableStatus == 1) {
        confirmInfo = "确认激活吗？";
    } else {
        confirmInfo = "确认封号吗吗？";
    }
    $.confirm(confirmInfo, function() {
        jqxhr = $.ajax({
            url: "/o2o/superAdmin/operationUser",
            data: {"userId": userId, "enableStatus": enableStatus},
            async: true,
            type: "POST",
            success: function(data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("操作成功！");
                    window.location.reload();
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType: "json"
        })
    })
}

//请求到客户端查询分页数据
function showUserListPage(name, page) {

    if(page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#dump-page-input").val("");
    $("#user-list").html("");

    jqxhr = $.ajax({
        url: "/o2o/superAdmin/getUserList",
        type: "POST",
        data: {"name": name, "page": page},
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if(data.success) {
                showUserList(data.pageInfo);
            } else {
                $.toast(data.errorMsg);
            }

        },
        dataType: "json"
    })
}


/**
 * 遍历店铺集合然后动态设置给html页面
 * @param shopList
 */
function showUserList(pageInfo) {

    var userList = pageInfo.list;
    //设置全局变量
    $page = pageInfo.pageNum;
    $totalPage = pageInfo.pages;

    if(userList == null || userList == '') {
        $("#dump-page").html("没有数据");
    }
    //操作项：如果该账号是可用就显示封号，如果是不可用就显示激活
    $.each(userList, function(i, user){
        var operation = "";
        var enableStatus = 0;
        var userType = "";
        if (user.enableStatus == 0) {
            operation = "激活";
            enableStatus = 1;
        } else {
            operation = "封号";
            enableStatus = 0;
        }


        $shopDiv = $("<div class='row row-shop'></div>")
            .append($("<div class='col-33'></div>").append(user.name))
            .append($("<div class='col-33'></div>").append(user.gender == 1 ? "男" : "女"))
            .append($("<div class='col-33'></div>").append($("<a class='operation'></a>").append(operation).attr("enable-status", enableStatus)
                                                                                                        .attr("user-id", user.userId)));
        $("#user-list").append($shopDiv);
    })
    //设置显示当前页数
    $("#current-page").html(pageInfo.pageNum);
}
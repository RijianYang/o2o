$(function () {
    showShopListPage(null, null);
    $("#next-page").click(function() {
        showShopListPage($("#search").val(), $page + 1, true);
    })
    $("#pre-page").click(function() {
        showShopListPage($("#search").val(), $page - 1, true);
    })
    $("#search-button").click(function() {
        showShopListPage($("#search").val(), 1, true);
    })
    $("#dump-page-button").click(function() {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if(!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showShopListPage($("#search").val(), $("#dump-page-input").val(), true);
    })

    //点击开张或倒闭
    $(document).on("click", ".operation", function() {
       let shopId = $(this).attr("shop-id");
       let enableStatus = $(this).attr("enable-status");
       operationShop(shopId, enableStatus);
    });

})

function operationShop(shopId, enableStatus) {
    var confirmInfo = ""
    if (enableStatus == 1) {
        confirmInfo = "确认开张吗？";
    } else {
        confirmInfo = "确认倒闭吗？";
    }
    $.confirm(confirmInfo, function() {
        jqxhr = $.ajax({
            url: "/o2o/superAdmin/operationShop",
            data: {"shopId": shopId, "enableStatus": enableStatus},
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
function showShopListPage(shopName, page) {

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
    $("#shop-list").html("");

    jqxhr = $.ajax({
        url: "/o2o/superAdmin/getShopList",
        type: "POST",
        data: {"shopName": shopName, "page": page},
        async: true,
        success: function (data) {

            ajaxRedirect(data);

            if(data.success) {

                showShopList(data.pageInfo);
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
function showShopList(pageInfo) {

    var shopList = pageInfo.list;
    //设置全局变量
    $page = pageInfo.pageNum;
    $totalPage = pageInfo.pages;

    if(shopList == null || shopList == '') {
        $("#dump-page").html("没有数据");
    }
    //动态设置店铺列表
    //状态：可用，不可用，审核中
    //操作：倒闭（可用），开张（不可用，审核中）
    $.each(shopList, function(i, shop){
        var status = "";
        var operation = "";
        var enableStatus = 0;
        if (shop.enableStatus == -1) {
            status = "不可用";
            operation = "开张";
            enableStatus = 1;
        } else if(shop.enableStatus == 0) {
            status = "审核中";
            operation = "开张";
            enableStatus = 1;
        } else {
            status = "可用";
            operation = "倒闭";
            enableStatus = -1;
        }


        $shopDiv = $("<div class='row row-shop'></div>")
            .append($("<div class='col-25'></div>").append(shop.shopName))
            .append($("<div class='col-25'></div>").append(status))
            .append($("<div class='col-25'></div>").append(shop.owner.name))
            .append($("<div class='col-25'></div>").append($("<a class='operation'></a>").append(operation).attr("enable-status", enableStatus)
                                                                                                        .attr("shop-id", shop.shopId)));
        $("#shop-list").append($shopDiv);
    })
    //设置显示当前页数
    $("#current-page").html(pageInfo.pageNum);
}
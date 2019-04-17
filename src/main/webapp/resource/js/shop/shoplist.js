$(function () {
    showShopListPage(null, null);
    $("#next-page").click(function() {
        showShopListPage($("#search").val(), $page + 1);
    })
    $("#pre-page").click(function() {
        showShopListPage($("#search").val(), $page - 1);
    })
    $("#search-button").click(function() {
        showShopListPage($("#search").val(), 1);
    })
    $("#dump-page-button").click(function() {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if(!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showShopListPage($("#search").val(), $("#dump-page-input").val());
    })
})

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
    $("#user-name").html("");
    $("#shop-list").html("");

    jqxhr = $.ajax({
        url: "/o2o/shop/getShopList",
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
    $.each(shopList, function(i, shop){
        $shopDiv = $("<div class='row row-shop'></div>")
            .append($("<div class='col-40'></div>").append(shop.shopName))
            .append($("<div class='col-40'></div>").append(calculateEnableStatus(shop.enableStatus)))
            .append($("<div class='col-20'></div>").append(shop.enableStatus == 1 ?
                $("<a></a>").prop("href", "/o2o/shop/shopManagePage?shopId="+shop.shopId).append("进入") : ""));
        $("#shop-list").append($shopDiv);
    })
    //设置显示当前页数
    $("#current-page").html(pageInfo.pageNum);
}
function calculateEnableStatus(enableStatus){
    if(enableStatus == -1) {
        return "不可用";
    } else if (enableStatus == 0) {
        return "审核中";
    } else if(enableStatus == 1) {
        return "可用";
    }
}
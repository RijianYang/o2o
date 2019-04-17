$(function () {
    var shopId = getQueryString("shopId");
    let getUserShopMapListUrl = "/o2o/userShopMap/getUserShopMapList";

    $("#return").prop("href", document.referrer);

    showUserShopMapList(shopId, getUserShopMapListUrl, null, null);
    $("#next-page").click(function () {
        showUserShopMapList(shopId, getUserShopMapListUrl, $("#search").val(), $page + 1);
    })
    $("#pre-page").click(function () {
        showUserShopMapList(shopId, getUserShopMapListUrl, $("#search").val(), $page - 1);
    })
    $("#search-button").click(function () {
        showUserShopMapList(shopId, getUserShopMapListUrl, $("#search").val(), 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showUserShopMapList(shopId, getUserShopMapListUrl, $("#search").val(), $("#dump-page-input").val());
    })

})


/**
 * 渲染商品列表页面
 * @param shopId
 * @param getUserShopMapListUrl
 */
function showUserShopMapList(shopId, getUserShopMapListUrl, buyerName, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#user-shop-map-list").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getUserShopMapListUrl,
        data: {"shopId": shopId, "buyerName": buyerName, "page": page},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let userShopMapList = data.pageInfo.list;
                $.each(userShopMapList, function (i, userShopMap) {
                    let userShopMapDiv = $("<div class='row row-shop'></div>")
                        .append($("<div class='col-50'></div>").append(userShopMap.buyer.name))
                        .append($("<div class='col-50'></div>").append(userShopMap.point))
                    $("#user-shop-map-list").append(userShopMapDiv);
                })
                $page = data.pageInfo.pageNum;
                $("#current-page").html($page);
                $totalPage = data.pageInfo.pages;
            } else {
                $.toast(data.errorMsg);
            }

        },
        dataType: "json"
    })
}
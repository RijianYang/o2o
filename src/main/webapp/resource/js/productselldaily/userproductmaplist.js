$(function () {
    var shopId = getQueryString("shopId");
    let getUserProductMapListUrl = "/o2o/productSellDaily/userProductMapList";

    $("#return").prop("href", document.referrer);
    getUserProductMapList(shopId, getUserProductMapListUrl, null, null, null);
    $("#next-page").click(function () {
        getUserProductMapList(shopId, getUserProductMapListUrl, $("#product-search").val(), $("#buyer-search").val(), $page + 1);
    })
    $("#pre-page").click(function () {
        getUserProductMapList(shopId, getUserProductMapListUrl, $("#product-search").val(), $("#buyer-search").val(), $page - 1,);
    })
    $("#search-button").click(function () {
        getUserProductMapList(shopId, getUserProductMapListUrl, $("#product-search").val(), $("#buyer-search").val(), 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        getUserProductMapList(shopId, getUserProductMapListUrl, $("#product-search").val(), $("#buyer-search").val(), $("#dump-page-input").val());
    })
})


/**
 * 渲染用户购买商品详情列表
 * @param shopId
 * @param getUserProductMapListUrl
 */
function getUserProductMapList(shopId, getUserProductMapListUrl, productName, buyerName, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#user-product-map-list").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getUserProductMapListUrl,
        data: {"shopId": shopId, "productName": productName, "page": page, "buyerName": buyerName},
        type: "POST",
        async: false,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let userProductMapList = data.pageInfo.list;
                $.each(userProductMapList, function (i, userProductMap) {
                    let userProductMapDiv = $("<div class='row row-shop'></div>")
                        .append($("<div class='col-20'></div>").append(userProductMap.product.productName))
                        .append($("<div class='col-20'></div>").append(new Date(userProductMap.createTime).Format("yyyy-MM-dd")))
                        .append($("<div class='col-20'></div>").append(userProductMap.buyer.name))
                        .append($("<div class='col-20'></div>").append(userProductMap.product.point))
                        .append($("<div class='col-20'></div>").append(userProductMap.operator.name));
                    $("#user-product-map-list").append(userProductMapDiv);
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
$(function () {
    shopId = getQueryString("shopId");
    $("#qrCode").prop("src", "/o2o/shopAuthMap/generateQRCodeForShopAuth?shopId=" + shopId);
    getShopAuthMapListUrl = "/o2o/shopAuthMap/getShopAuthMapList";
    $("#return").prop("href", "/o2o/shop/shopManagePage?shopId="+shopId);
    showShopAuthMapListPage(shopId, getShopAuthMapListUrl, null);
    $("#next-page").click(function () {
        showShopAuthMapListPage(shopId, getShopAuthMapListUrl, $page + 1);
    })
    $("#pre-page").click(function () {
        showShopAuthMapListPage(shopId, getShopAuthMapListUrl, $page - 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showShopAuthMapListPage(shopId, getShopAuthMapListUrl, $("#dump-page-input").val());
    })

})

/**
 * 渲染授权列表页面
 * @param shopId
 * @param getProductListUrl
 */
function showShopAuthMapListPage(shopId, getProductListUrl, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不等于空说明是点击上下页或者跳转页面，已经请求过后台了(加载页面时传过来的是null)，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#auth-list").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getProductListUrl,
        data: {"shopId": shopId, "page": page},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let shopAuthMapList = data.pageInfo.list;
                $.each(shopAuthMapList, function (i, shopAuthMap) {
                    if (shopAuthMap.titleFlag != 0) {
                        var enableStatus = shopAuthMap.enableStatus == 0 ? "复原" : "删除";
                        var authDiv = $("<div class='row row-shop'></div>")
                            .append($("<div class='col-33'></div>").append(shopAuthMap.employee.name))
                            .append($("<div class='col-33'></div>").append(shopAuthMap.title))
                            .append($("<div class='col-33'></div>")
                                .append($("<div class='row'></div>")
                                    .append($("<div class='col-50'></div>").append($("<a>编辑</a>").prop("href",
                                        "/o2o/shopAuthMap/shopAuthMapEditPage?shopId="+shopId+"&shopAuthMapId=" + shopAuthMap.shopAuthMapId)))
                                    .append($("<div class='col-50'></div>").append($("<a class='del-or-recover'>" + enableStatus + " </a>").attr("shop-auth-map-id", shopAuthMap.shopAuthMapId)
                                                                                                                    .attr("enable-status", shopAuthMap.enableStatus)))
                                ));
                    } else {
                        var authDiv = $("<div class='row row-shop'></div>")
                            .append($("<div class='col-33'></div>").append(shopAuthMap.employee.name))
                            .append($("<div class='col-33'></div>").append(shopAuthMap.title))
                            .append($("<div class='col-33'></div>")
                                .append($("<div class='row'></div>")
                                    .append($("<div class='col-100'></div>").append("不可操作"))
                                ));
                    }
                    $("#auth-list").append(authDiv);

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
$(document).on("click", ".del-or-recover", function() {
    var enableStatus = $(this).attr("enable-status");
    var shopAuthMapId = $(this).attr("shop-auth-map-id");
    var confirmInfo = "";
    if (enableStatus == 0) {
        confirmInfo = "确认复原吗？";
        enableStatus = 1;
    } else {
        confirmInfo = "确认删除吗？";
        enableStatus = 0;
    }
    $.confirm(confirmInfo, function () {
        jqxhr = $.ajax({
            url: "/o2o/shopAuthMap/modifyShopAuthMapById",
            data: {"enableStatus": enableStatus, "isCode": false, "shopAuthMapId": shopAuthMapId},
            async: true,
            type: "POST",
            success: function(data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("操作成功！");
                    showShopAuthMapListPage(shopId, getShopAuthMapListUrl, $page);
                } else {
                    $.toast(data.errorMsg);
                }
            }
        })
    })
})

$(function () {
    var shopId = getQueryString("shopId");
    let getProductListUrl = ("/o2o/product/getProductList");

    $("#return").prop("href", "/o2o/shop/shopManagePage?shopId" + shopId);
    $("#add-product").prop("href", "/o2o/product/productAddPage?shopId=" + shopId);
    showProductListPage(shopId, getProductListUrl, null, null);
    $("#next-page").click(function () {
        showProductListPage(shopId, getProductListUrl, $("#search").val(), $page + 1);
    })
    $("#pre-page").click(function () {
        showProductListPage(shopId, getProductListUrl, $("#search").val(), $page - 1);
    })
    $("#search-button").click(function () {
        showProductListPage(shopId, getProductListUrl, $("#search").val(), 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showProductListPage(shopId, getProductListUrl, $("#search").val(), $("#dump-page-input").val());
    })

    $(document).on("click", ".sold-out", function () {
        let soldOutOrInProductUrl = "/o2o/product/soldOutOrInProduct";
        let productId = $(this).attr("pid");
        let enableStatus = $(this).attr("enable-status");
        soldOutOrInProduct(enableStatus, soldOutOrInProductUrl, productId, shopId);
    })
})

function soldOutOrInProduct(enableStatus, soldOutOrInProductUrl, productId, shopId) {
    if (enableStatus == 0) {
        var confirmInfo = "确认上架吗？";
        enableStatus = 1;
    } else {
        var confirmInfo = "确认下架吗？";
        enableStatus = 0;
    }
    $.confirm(confirmInfo, function () {
        jqxhr = $.ajax({
            url: soldOutOrInProductUrl,
            data: {"productId": productId, "shopId": shopId, "enableStatus": enableStatus},
            type: "POST",
            async: true,
            success: function (data) {
                ajaxRedirect(data);
                if (data.success) {
                    if (enableStatus == 1) {
                        $.toast("上架成功");
                    } else {
                        $.toast("下架成功");
                    }
                    showProductListPage(shopId, "/o2o/product/getProductList", $("#search").val(), $page);
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType: "json"
        })
    })
}

/**
 * 渲染商品列表页面
 * @param shopId
 * @param getProductListUrl
 */
function showProductListPage(shopId, getProductListUrl, productName, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#product-list").html("");
    $("#user-name").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getProductListUrl,
        data: {"shopId": shopId, "productName": productName, "page": page},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let productList = data.pageInfo.list;
                $.each(productList, function (i, product) {
                    //状态为0，说明是不可用状态，此时需要显示为上架，反之亦然
                    if (product.enableStatus == 0) {
                        var enableStatus = "上架";
                    } else {
                        var enableStatus = "下架";
                    }
                    let productDiv = $("<div class='row row-shop'></div>")
                        .append($("<div class='col-33'></div>").append(product.productName))
                        .append($("<div class='col-33'></div>").append(product.point))
                        .append($("<div class='col-33'></div>")
                            .append($("<div class='row'></div>")
                                .append($("<div class='col-33'></div>").append($("<a>编辑</a>").prop("href",
                                    "/o2o/product/productEditPage?shopId=" + shopId + "&productId=" + product.productId)))
                                .append($("<div class='col-33'></div>").append($("<a class='sold-out'>"+enableStatus+"</a>").attr("pid", product.productId)
                                    .attr("enable-status", product.enableStatus)))
                                .append($("<div class='col-33'></div>").append($("<a>预览</a>").prop("href", "/o2o/loginFrontEnd/productDetailPage?shopId="+product.shop.shopId+"&parentId=&productId="+product.productId)))
                            ));
                    $("#product-list").append(productDiv);
                })
                if (productList == null || productList == "") {
                    $("#dump-page").html("没有数据");
                    return;
                }
                $("#user-name").html(data.user.name);
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
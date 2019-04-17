$(function() {
    let shopId = getQueryString("shopId");
    let parentId = getQueryString("parentId");
    let productId = getQueryString("productId");

    $("#return").prop("href", "/o2o/frontEnd/shopDetailPage?shopId="+shopId+"&parentId=" + parentId);

    $('#me').click(function () {
        $.openPanel('#panel-js-demo');
    });
    $.init();

    //获取商品信息
    getProductInfoById(productId);
})

/**
 * 根据商品主键查询出商品信息
 * @param productId
 */
function getProductInfoById(productId) {
    jqxhr = $.ajax({
        url: "/o2o/loginFrontEnd/getProductInfoById",
        data: {"productId": productId},
        type: "POST",
        async: true,
        success: function(data) {
            ajaxRedirect(data);
            if (data.success) {
                let product = data.product;
                let productImgList = data.productImgList;
                initProductInfo(product);
                initProductImg(productImgList);
                showSildeBar(data.user);
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType: "json"
    })
}

/**
 * 初始化商品信息
 * @param product
 */
function initProductInfo(product) {
    $("#title").append(product.productName);
    $("#product-img").prop("src", product.imgAddr);
    $("#product-last-edit-time").append(product.lastEditTime);
    $("#product-normal-price").append(product.normalPrice);
    $("#product-promotion-price").append(product.promotionPrice);
    $("#product-desc").append(product.productDesc);
    $("#product-point").append("积分：" + product.point);

    //获取商品二维码
    $("#qrCode").prop("src", "/o2o/loginFrontEnd/generateQRCodeForSaleProduct?shopId=" + product.shop.shopId + "&productId=" + product.productId);
}

/**
 * 初始化商品详情图片集合
 * @param productImgList
 */
function initProductImg(productImgList) {
    var productImgDiv = ""
    $.each(productImgList, function(i, productImg) {
        let $productImgDiv = $("<div class='card-content'></div>").
                            append($("<img width='100%'>").prop("src", productImg.imgAddr));
        $("#product-img-list").append($productImgDiv);
    })
}
$(".item-input").on("change", ".detail-img:last-child", function () {
    if ($(".detail-img").length < 6) {
        $("#detail-img").append($("<input type='file' class='detail-img'>"));
    }

})

$(function () {
    //页面一加载就把对应的店铺所有的商品分类获取到
    let shopId = getQueryString("shopId");

    $("#back").prop("href", "/o2o/product/productListPage?shopId=" + shopId);

    let getProductCategoryUrl = "/o2o/productCategory/getProductCategoryListByShopId";
    let addProductUrl = "/o2o/product/addProduct";
    let modifyProductUrl = "/o2o/product/modifyProduct";
    let echoProductUrl = "/o2o/product/getProductById";

    productId = getQueryString("productId");
    getProductCategory(getProductCategoryUrl, shopId);
    $("#submit").click(function () {
        addOrModifyProduct(addProductUrl, modifyProductUrl, shopId);
    })

    //页面一加载就把商品信息回显
    if(productId){
        echoProduct(echoProductUrl, productId);
    }
})

/**
 * 回显商品页面
 */
function echoProduct(echoProductUrl, productId) {
    jqxhr = $.ajax({
        url : echoProductUrl,
        data : {"productId" : productId},
        type : "POST",
        async : true,
        success : function(data) {
          if(data.success) {
              let product = data.product;
              $("#product-name").val(product.productName);
              $("#priority").val(product.priority);
              $("#normal-price").val(product.normalPrice);
              $("#promotion-price").val(product.promotionPrice);
              $("#product-desc").val(product.productDesc);
              $("#point").val(product.point);
              $("#category").html("");
              $("#category").append($("<option></option>").append(product.productCategory.productCategoryName).
                                attr("data-id", product.productCategory.productCategoryId)).prop("disabled", true);
          } else {
              $.toast(data.errorMsg);
          }
        },
        dataType : "json"
    })
}

/**
 * 添加一个商品
 * @param addProductUrl
 * @param shopId
 */
function addOrModifyProduct(addProductUrl, modifyProductUrl, shopId) {
    var formData = new FormData();
    let productName = $("#product-name").val();
    let productCategoryId = $("#category").find("option").not(function () {
        return !this.selected;
    }).attr("data-id");
    let priority = $("#priority").val();
    let normalPrice = $("#normal-price").val();
    let promotionPrice = $("#promotion-price").val();
    let productDesc = $("#product-desc").val();
    let briefImg = $("#briefImg")[0].files[0];
    let point = $("#point").val();
    let verifyCode = $("#j_captcha").val();

    formData.append("productName", productName);
    formData.append("productCategory.productCategoryId", productCategoryId);
    formData.append("priority", priority);
    formData.append("normalPrice", normalPrice);
    formData.append("promotionPrice", promotionPrice);
    formData.append("productDesc", productDesc);
    formData.append("point", point);
    if(briefImg != "undefined") {
        formData.append("briefImg", briefImg);
    }
    formData.append("briefImg", briefImg);
    formData.append("verifyCode", verifyCode);

    let detailImgs = $(".detail-img");
    $.each(detailImgs, function (i, m) {
        if (m.files[0] != null && m.files[0] != "") {
            formData.append("detailImgs", m.files[0]);
        }
    })


    formData.append("shop.shopId", shopId);
    if (productId) {
        formData.append("productId", productId);
    }

    if (checkForm(formData)) {
        jqxhr =  $.ajax({
            url: productId ? modifyProductUrl : addProductUrl,
            type: "POST",
            data: formData,
            async: true,
            processData: false,
            contentType: false,
            success: function (data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("操作成功");
                    //操作成功后就跳转到商品列表页面
                    window.location.href = "/o2o/product/productListPage?shopId=" + shopId;
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType: "json"
        })
    }
}

/**
 * 校验表单数据输入是否规范
 * @param formData
 */
function checkForm(formData) {
    let productName = formData.get("productName");
    let productCategoryId = formData.get("productCategory.productCategoryId");
    let priority = formData.get("priority");
    let normalPrice = formData.get("normalPrice");
    let promotionPrice = formData.get("promotionPrice");
    let productDesc = formData.get("productDesc");
    let briefImg = formData.get("briefImg");
    let verifyCode = formData.get("verifyCode");
    let point = formData.get("point");

    if (productName == null || productName.trim() == "") {
        $.toast("请填写商品名称");
        return false;
    }
    if (productName.length < 2 || productName.length > 10) {
        $.toast("商品名称为2-10之间");
        return false;
    }


    if (!productId && productCategoryId == "no") {
        $.toast("请选择商品类别");
        return false;
    }

    if (priority == null || priority.trim() == "") {
        $.toast("请填写优先级");
        return false;
    }
    if (priority < 0) {
        $.toast("优先级不能为负数");
        return false;
    }

    if (point == null || point.trim() == "") {
        $.toast("请填写积分");
        return false;
    }
    if (point < 0) {
        $.toast("积分不能为负数");
        return false;
    }

    if (normalPrice == null || normalPrice.trim() == "") {
        $.toast("请填写原价");
        return false;
    }
    if (normalPrice < 0) {
        $.toast("原价不能为负数");
        return false;
    }

    if (promotionPrice == null || promotionPrice.trim() == "") {
        $.toast("请填写折扣价");
        return false;
    }
    if (promotionPrice < 0) {
        $.toast("折扣价不能为负数");
        return false;
    }
    if (parseFloat(promotionPrice) >= parseFloat(normalPrice)) {
        $.toast("折扣价必须小于原价");
        return false;
    }

    if (!productId && briefImg == "undefined") {
        $.toast("请上传缩略图");
        return false;
    }
    if (!productId && briefImg.type.indexOf("image") == -1) {
        //如果等于-1说明不包含该字符串
        $.toast("请上传正确格式的缩略图!");
        return false;
    }

    let detailImgs = $(".detail-img");
    var flag = false;
    $.each(detailImgs, function (i, m) {
        if (m.files[0] != "undefined" && m.files[0] != null) {
            flag = true;
            if (m.files[0].type.indexOf("image") == -1) {
                //如果等于-1说明不包含该字符串
                $.toast("请上传正确格式的详情图!");
                return false;
            }
        }
    })
    //如果没有一个上传详情图片
    if (!productId && !flag) {
        $.toast("请上传至少一张详情图");
        return false;
    }

    if (productDesc == null || productDesc.trim() == "") {
        $.toast("请填写商品描述");
        return false;
    }
    if (productDesc.length > 300) {
        $.toast("商品描述最多为300");
        return false;
    }

    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请填写验证码");
        return false;
    }
    return true;
}


/**
 * 回显该店铺中所有的商品类别下拉列表
 * @param getProductCategoryUrl
 * @param shopId
 */
function getProductCategory(getProductCategoryUrl, shopId) {
    jqxhr = $.ajax({
        url: getProductCategoryUrl,
        data: {"shopId": shopId},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if(data.success) {
                $.each(data.productCategoryList, function (i, m) {
                    let $productCategoryOp = $("<option></option>").attr("data-id", m.productCategoryId)
                        .append(m.productCategoryName);
                    $("#category").append($productCategoryOp);
                })
            } else {
                $.toast(data.errorMsg);
            }

        },
        dataType: "json"
    })
}



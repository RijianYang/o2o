$(function () {
    shopId = getQueryString("shopId");
    parentId = getQueryString("parentId");

    $("#award-convert").click(function() {
        window.location.href = "/o2o/loginFrontEnd/awardListPage?userType=1&shopId="+shopId+"&parentId="+parentId;
        window.reload
    })

    if (parentId == "null" || parentId == "") {
        $("#return").prop("href", "/o2o/frontEnd/frontEndShopPage");
    } else {
        $("#return").prop("href", "/o2o/frontEnd/frontEndShopPage?parentId=" + parentId);
    }
    //显示前端店铺信息(一级或二级店铺类别列表)区域列表
    let showProductPageInfoUrl = "/o2o/frontEnd/showProductPageInfo";
    showProductPageInfo(showProductPageInfoUrl, shopId);

    addItem(null, shopId, null, null, null);
    //下滑屏幕自动进行分页搜索
    inTheScreen();
    //点击商品详情页面
    $('.product-list').on("click", ".card", function () {
        var shopId = $(this).attr("product-id");
        window.location.href = "#";
    });

    //点击类别按钮
    productCategoryBtn()
    //搜索框
    search();
    $('#me').click(function () {
        $.openPanel('#panel-js-demo');
    });
    $.init();

    //点击查看商品信息
    $(document).on("click", ".show-product", function () {
        let productId = $(this).attr("product-id");
        let parentId = getQueryString("parentId");
        let shopId = getQueryString("shopId");
        window.location.href = "/o2o/loginFrontEnd/productDetailPage?shopId="+shopId + "&parentId=" + parentId + "&productId=" + productId;
    })

})

/**
 * 滑屏幕自动进行分页搜索
 */
function inTheScreen() {
    $(document).on('infinite', '.infinite-scroll-bottom', function () {
        if (loading) {
            return;
        }
        let productCategoryId = $(".button-fill").attr("product-category-id");
        let productName = $("#search").val();
        addItem(pageInfo.pageNum, shopId, productCategoryId, productName);
    });
}

/**
 * 搜索框
 */
function search() {
    $('#search').on('input', function (e) {
        $('.list-div').empty();
        let page = 1;
        let productCategoryId = $(".button-fill").attr("product-category-id");
        let productName = $("#search").val();
        addItem(page, shopId, productCategoryId, productName);
    });
}

/**
 * 点击商品类别按钮
 */
function productCategoryBtn() {
    $('#productlist-search-div').on(
        'click',
        '.button',
        function (e) {
            var productCategoryId = $(this).attr("product-category-id");

            if ($(this).hasClass('button-fill')) {
                $(this).removeClass('button-fill');
                productCategoryId = '';
            } else {
                //$(this).siblings()获取除了自身该种类型的集合
                $(this).addClass('button-fill').siblings()
                    .removeClass('button-fill');
            }
            //重置店铺页面
            $('.list-div').empty();
            //重置当前页为1
            let page = 1;
            let productName = $("#search").val();
            addItem(page, shopId, productCategoryId, productName);

        });
}

function showProductPageInfo(showProductPageInfoUrl, shopId) {
    jqxhr = $.ajax({
        url: showProductPageInfoUrl,
        data: {"shopId": shopId},
        type: "POST",
        async: false,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);
                showProductCategoryList(data.productCategroyList);
                showShopInfo(data.shop);
            } else {
                $.toast(data.errorMsg);
            }
        },
        error: function () {
            alert("aa");
        },
        dataType: "json"
    })
}

/**
 * 显示店铺信息
 */
function showShopInfo(shop) {
    $("#shop-name").append(shop.shopName);
    let shopInfo = $("<div class='card demo-card-header-pic'></div>")
        .append($("<div valign='bottom' class='card-header color-white no-border no-padding'></div>").append($("<img class='card-cover'>").prop("src", shop.shopImg)))
        .append($("<div class='card-content'></div>").append($("<div class='card-content-inner'></div>").append($("<p class='color-gray'></p>").append(new Date(shop.lastEditTime).Format("yyyy-MM-dd"))).append($("<p></p>").append(shop.shopDesc))))
        .append($("<div class='card-footer'></div>").append($("<span></span>").append(shop.shopAddr)).append($("<span></span>").append("号码："+shop.phone)))
    $("#shop-info").append(shopInfo);
}

/**
 * 动态拼装商品分类列表
 * @param shopCategoryList
 */
function showProductCategoryList(productCategoryList) {
    $.each(productCategoryList, function (i, productCategory) {
        let productCategoryButton = $("<a href='#' class='button'></a>").append(productCategory.productCategoryName).attr("product-category-id", productCategory.productCategoryId)
        $("#productlist-search-div").append(productCategoryButton);

    })
}

/**
 * 分页查询
 * @param page
 * @param parentId
 * @param areaId
 * @param shopCategoryId
 * @param shopName
 */
function addItem(page, shopId, productCategoryId, productName) {

    $("#bottom").html("");

    if (productCategoryId == undefined) {
        productCategoryId = null;
    }

    let showProductListUrl = "/o2o/frontEnd/showProductList";
    //设定加载符，若还在后台取数据则不能再次访问后台
    loading = true;
    //访问后台获取相应查询条件下的店铺列表
    jqxhr = $.ajax({
        url: showProductListUrl,
        data: {
            "page": page, "shopId": shopId, "productCategory.productCategoryId":
            productCategoryId, "productName": productName
        },
        async: false,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                pageInfo = data.pageInfo;
                if (pageInfo == null) {
                    $(".product-list").append("没有数据！");
                    showSildeBar(data.user);
                    return;
                }
                //获取当前查询条件下店铺的总数
                let maxItems = pageInfo.total;
                //遍历集合中取出来的店铺列表
                var shopHtml = "";
                $.each(pageInfo.list, function (i, product) {
                    shopHtml +=
                        "<div class='card'>" +
                        "<div class='card-header'>" + product.productName + "</div>" +
                        "<div class='card-content'>" +
                        "<div class='list-block media-list'>" +
                        "<ul>" +
                        "<li class='item-content'>" +
                        "<div class='item-media'>" +
                        "<img src='" + product.imgAddr + "' width='44'>" +
                        "</div>" +
                        "<div class='item-inner'>" +
                        "<div class='item-subtitle'>" + product.productDesc + "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>" +
                        "</div>" +
                        "</div>" +
                        "<div class='card-footer'>" +
                        "<span>" + new Date(product.lastEditTime).Format("yyyy-MM-dd") + "更新</span>" +
                        "<span class='show-product' product-id='" + product.productId + "'>点击查看</span>" +
                        "</div>" +
                        "</div>";
                })
                //将卡片集合添加到目标html组件里
                $(".list-div").append(shopHtml);
                //获取目前位置已显示的卡片种数，包含之前已经加载好的
                var total = $(".list-div .card").length;
                //若总数达到跟按照此查询条件列出来的总数一致，则停止后台加载
                if (total >= maxItems) {
                    $(".infinite-scroll-preloader").hide();
                    $("#bottom").html("到底了");
                } else {
                    $(".infinite-scroll-preloader").show();
                    //否则页面加1，继续load新的店铺
                    pageInfo.pageNum = pageInfo.pageNum + 1;
                    //加载结束，可以再次加载了
                    loading = false;
                }
            } else {
                //为了避免搜索框一输入数据就会请求到后台(包括打字时候的字母这时候就会查不到信息然后就会报错)这里就不显示错误信息了
            }
        },
        dataType: "json"
    })
}

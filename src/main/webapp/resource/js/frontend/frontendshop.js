$(function () {
    $("#return").prop("href", "/o2o");
    let parentId = getQueryString("parentId");
    //显示前端店铺信息(一级或二级店铺类别列表)区域列表
    let showShopInfoUrl = "/o2o/frontEnd/showShopInfo";
    showShopInfo(showShopInfoUrl, parentId);
    addItem(null, parentId, null, null, null);
    //下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading) {
            return;
        }
        let areaId = $("#area-search").val();

        let shopCategoryId = $(".button-fill").attr("shop-category-id");
        let shopName = $("#search").val();
        addItem(pageInfo.pageNum, parentId, areaId, shopCategoryId, shopName);
    });

    //点击查看页面
    $(document).on("click", ".show-shop-info", function() {
        let shopId = $(this).attr("shop-id");
        window.location.href = "/o2o/frontEnd/shopDetailPage?shopId="+shopId+"&parentId="+parentId+"";
    })

    //点击类别页面
    $('#shoplist-search-div').on(
        'click',
        '.button',
        function(e) {
            var shopCategoryId = $(this).attr("shop-category-id");

            if ($(this).hasClass('button-fill')) {
                $(this).removeClass('button-fill');
                shopCategoryId = '';
            } else {
                //$(this).siblings()获取除了自身该种类型的集合
                $(this).addClass('button-fill').siblings()
                    .removeClass('button-fill');
            }
            //重置店铺页面
            $('.list-div').empty();
            //重置当前页为1
            let page = 1;
            let areaId = $("#area-search").val();
            let shopName = $("#search").val();
            addItem(page, parentId, areaId, shopCategoryId, shopName);

        });

    //搜索框
    $('#search').on('input', function(e) {
        $('.list-div').empty();
        let page = 1;
        let shopCategoryId = $(".button-fill").attr("shop-category-id");
        let areaId = $("#area-search").val();
        let shopName = $("#search").val();
        addItem(page, parentId, areaId, shopCategoryId, shopName);
    });

    //点击区域下拉列表
    $('#area-search').on('change', function() {
        $('.list-div').empty();
        let page = 1;
        let shopCategoryId = $(".button-fill").attr("shop-category-id");
        let areaId = $("#area-search").val();
        let shopName = $("#search").val();
        addItem(page, parentId, areaId, shopCategoryId, shopName);
    });

    $('#me').click(function() {
        $.openPanel('#panel-js-demo');
    });

    $.init();
})


function showShopInfo(showShopInfoUrl, parentId) {
    jqxhr = $.ajax({
        url: showShopInfoUrl,
        data: {"parentId": parentId},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);
                showShopCategoryList(data.shopCategoryList);
                showAreaList(data.areaList);
            } else {
                $.toast(data.errorMsg);
            }

        },
        dataType: "json"
    })
}

/**
 * 动态拼装一级或二级店铺列表
 * @param shopCategoryList
 */
function showShopCategoryList(shopCategoryList) {
    //<a href="#" class="button button-fill">所有货物</a>
    $("#shoplist-search-div").append("<a href='#' class='button '>全部类别</a>")
    $.each(shopCategoryList, function (i, shopCategory) {
        let shopCategoryButton = $("<a href='#' class='button'></a>").append(shopCategory.shopCategoryName).attr("shop-category-id", shopCategory.shopCategoryId)

        $("#shoplist-search-div").append(shopCategoryButton);

    })
}

/**
 * 动态拼装区域列表
 * @param areaList
 */
function showAreaList(areaList) {
    $.each(areaList, function (i, area) {
        //<option">全部区域</option>
        let areaOption = $("<option></option>").prop("value", area.areaId).append(area.areaName);
        $("#area-search").append(areaOption);
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
function addItem(page, parentId, areaId, shopCategoryId, shopName) {

    $("#bottom").html("");

    if(shopCategoryId == undefined) {
        shopCategoryId = null;
    }
    let urlParentId = getQueryString("parentId");
    if(!urlParentId) {
        //如果地址栏上没有parentId说明是点击全部店铺进入的
        //那么这时候的店铺类别肯定是一级类别,所以类别按钮上的类别
        parentId = shopCategoryId;
        shopCategoryId = null;
    }
    let showFrontEndShopUrl = "/o2o/frontEnd/showFrontEndShop";
    //设定加载符，若还在后台取数据则不能再次访问后台
    loading = true;
    //访问后台获取相应查询条件下的店铺列表
    jqxhr = $.ajax({
        url: showFrontEndShopUrl,
        data: {
            "page": page, "shopCategory.parent.shopCategoryId": parentId, "area.areaId": areaId,
            "shopCategory.shopCategoryId": shopCategoryId, "shopName": shopName
        },
        async: false,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
               pageInfo = data.pageInfo;
                if (pageInfo == null) {
                    $(".shop-list").append("没有数据！");
                    showSildeBar(data.user);
                    return;
                }
               //获取当前查询条件下店铺的总数
               let maxItems = pageInfo.total;
               //遍历集合中取出来的店铺列表
                var shopHtml = "";
                $.each(pageInfo.list, function (i, shop) {
                   shopHtml +=
                   "<div class='card' shop-id='"+shop.shopId+"'>"+
                        "<div class='card-header'>"+shop.shopName+"</div>"+
                        "<div class='card-content'>"+
                            "<div class='list-block media-list'>"+
                                "<ul>"+
                                    "<li class='item-content'>"+
                                        "<div class='item-media'>"+
                                            "<img src='"+shop.shopImg+"' width='44'>"+
                                        "</div>"+
                                        "<div class='item-inner'>"+
                                            "<div class='item-subtitle'>"+shop.shopDesc+"</div>"+
                                        "</div>"+
                                    "</li>"+
                                "</ul>"+
                            "</div>"+
                        "</div>"+
                        "<div class='card-footer'>"+
                            "<span>"+new Date(shop.lastEditTime).Format("yyyy-MM-dd")+"更新</span>"+
                            "<span shop-id="+shop.shopId+" class ='show-shop-info'>点击查看</span>"+
                        "</div>"+
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

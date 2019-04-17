$(function () {

    addItem(null, null);
    //下滑屏幕自动进行分页搜索
    inTheScreen();


    //搜索框
    search();
    $('#me').click(function () {
        $.openPanel('#panel-js-demo');
    });
    $.init();
})


/**
 * 滑屏幕自动进行分页搜索
 */
function inTheScreen() {
    $(document).on('infinite', '.infinite-scroll-bottom', function () {
        if (loading) {
            return;
        }
        let shopName = $("#search").val();
        addItem(pageInfo.pageNum,  shopName);
    });
}

/**
 * 搜索框
 */
function search() {
    $('#search').on('input', function (e) {
        $('.list-div').empty();
        let page = 1;
        let shopName = $("#search").val();
        addItem(page, shopName);
    });
}


/**
 * 分页查询
 */
function addItem(page, shopName){

    $("#bottom").html("");


    let showUserShopMapListUrl = "/o2o/loginFrontEnd/getUserShopMapList";
    //设定加载符，若还在后台取数据则不能再次访问后台
    loading = true;
    //访问后台获取相应查询条件下的店铺列表
    jqxhr = $.ajax({
        url: showUserShopMapListUrl,
        data: {
            "page": page, "shopName": shopName},
        async: false,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);

                pageInfo = data.pageInfo;
                if (pageInfo == null) {
                    $(".user-shop-map-list").append("没有数据！");
                    return;
                }
                //获取当前查询条件下我的店铺积分的总数
                let maxItems = pageInfo.total;
                //遍历集合中取出来的店铺列表
                var userShopMapHtml = "";
                $.each(pageInfo.list, function (i, userShopMap) {
                    userShopMapHtml +=
                        "<div class='card'>" +
                        "<div class='card-header'>" + userShopMap.shop.shopName +"</div>"+
                        "<div class='card-content'>" +
                        "<div class='list-block media-list'>" +
                        "<ul>" +
                        "<li class='item-content'>" +
                        "<div class='item-media'>" +
                        "<img src='" + userShopMap.shop.shopImg + "' width='44'>" +
                        "</div>" +
                        "<div class='item-inner'>" +
                        "<div class='item-subtitle'>本店积分：" + userShopMap.point + "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>" +
                        "</div>" +
                        "</div>" +
                        "<div class='card-footer'>" +
                        "<span>" + new Date(userShopMap.lastEditTime).Format("yyyy-MM-dd hh:mm:ss") + "更新</span>" +
                        "</div>" +
                        "</div>";
                })
                $(".user-shop-map-list").append(userShopMapHtml);
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

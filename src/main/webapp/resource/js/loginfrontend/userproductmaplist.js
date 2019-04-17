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
        let productName = $("#search").val();
        addItem(pageInfo.pageNum,  productName);
    });
}

/**
 * 搜索框
 */
function search() {
    $('#search').on('input', function (e) {
        $('.list-div').empty();
        let page = 1;
        let productName = $("#search").val();
        addItem(page, productName);
    });
}


/**
 * 分页查询
 */
function addItem(page, productName){

    $("#bottom").html("");


    let showUserProductMapListUrl = "/o2o/loginFrontEnd/getUserProductMapList";
    //设定加载符，若还在后台取数据则不能再次访问后台
    loading = true;
    //访问后台获取相应查询条件下的店铺列表
    jqxhr = $.ajax({
        url: showUserProductMapListUrl,
        data: {
            "page": page, "productName": productName},
        async: false,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);

                pageInfo = data.pageInfo;
                if (pageInfo == null) {
                    $(".user-product-map-list").append("没有数据！");
                    return;
                }
                //获取当前查询条件下我的店铺积分的总数
                let maxItems = pageInfo.total;
                //遍历集合中取出来的店铺列表
                var userProductMapHtml = "";
                $.each(pageInfo.list, function (i, userProductMap) {
                    userProductMapHtml +=
                        "<div class='card'>" +
                        "<div class='card-header'>" + userProductMap.shop.shopName +"</div>"+
                        "<div class='card-content'>" +
                        "<div class='list-block media-list'>" +
                        "<ul>" +
                        "<li class='item-content'>" +
                        "<div class='item-media'>" +
                        "<img src='" + userProductMap.product.imgAddr + "' width='44'>" +
                        "</div>" +
                        "<div class='item-inner'>" +
                        "<div class='item-subtitle'>" + userProductMap.product.productName + "</div>" +
                        "</div>" +
                        "</li>" +
                        "</ul>" +
                        "</div>" +
                        "</div>" +
                        "<div class='card-footer'>" +
                        "<span>" + new Date(userProductMap.createTime).Format("yyyy-MM-dd hh:mm:ss") + "</span>" +
                        "<span class='show-award'>获取积分:"+userProductMap.product.point+"</span>" +
                        "</div>" +
                        "</div>";
                })
                $(".user-product-map-list").append(userProductMapHtml);
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

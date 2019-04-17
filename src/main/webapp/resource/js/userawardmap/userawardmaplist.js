$(function () {
    var shopId = getQueryString("shopId");
    let getUserAwardMapListUrl = "/o2o/userAwardMap/getUserAwardMapList";

    $("#return").prop("href", document.referrer);

    showUserAwardMapList(shopId, getUserAwardMapListUrl, null, null);
    $("#next-page").click(function () {
        showUserAwardMapList(shopId, getUserAwardMapListUrl, $("#search").val(), $page + 1);
    })
    $("#pre-page").click(function () {
        showUserAwardMapList(shopId, getUserAwardMapListUrl, $("#search").val(), $page - 1);
    })
    $("#search-button").click(function () {
        //搜索的不用传当前页数，如果在第二页，使用搜索就会产生搜不到的情况，所以这里搜索从第一页开始搜
        showUserAwardMapList(shopId, getUserAwardMapListUrl, $("#search").val(), 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showUserAwardMapList(shopId, getUserAwardMapListUrl, $("#search").val(), $("#dump-page-input").val());
    })

})


/**
 * 渲染商品列表页面
 * @param shopId
 * @param getUserAwardMapListUrl
 */
function showUserAwardMapList(shopId, getUserAwardMapListUrl, buyerName, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#user-award-map-list").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getUserAwardMapListUrl,
        data: {"shopId": shopId, "buyerName": buyerName, "page": page},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let userAwardMapList = data.pageInfo.list;
                $.each(userAwardMapList, function (i, userAwardMap) {
                    let userAwardMapDiv = $("<div class='row row-shop'></div>")
                        .append($("<div class='col-20'></div>").append(userAwardMap.award.awardName))
                        .append($("<div class='col-20'></div>").append(new Date(userAwardMap.createTime).Format("yyyy-MM-dd")))
                        .append($("<div class='col-20'></div>").append(userAwardMap.buyer.name))
                        .append($("<div class='col-20'></div>").append(userAwardMap.award.point))
                        .append($("<div class='col-20'></div>").append(userAwardMap.operator.name))
                    $("#user-award-map-list").append(userAwardMapDiv);
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
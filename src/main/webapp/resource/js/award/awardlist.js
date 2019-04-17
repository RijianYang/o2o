$(function () {
    var shopId = getQueryString("shopId");
    let getAwardListUrl = "/o2o/award/getAwardList";

    $("#return").prop("href", "/o2o/shop/shopManagePage?shopId="+shopId);
    $("#add-award").prop("href", "/o2o/award/awardAddPage?shopId=" + shopId);
    showAwardListPage(shopId, getAwardListUrl, null, null);

    $("#next-page").click(function () {
        showAwardListPage(shopId, getAwardListUrl, $("#search").val(), $page + 1);
    })
    $("#pre-page").click(function () {
        showAwardListPage(shopId, getAwardListUrl, $("#search").val(), $page - 1);
    })
    $("#search-button").click(function () {
        showAwardListPage(shopId, getAwardListUrl, $("#search").val(), 1);
    })
    $("#dump-page-button").click(function () {
        let digitReg = /^[0-9]*$/;
        let page = $("#dump-page-input").val();
        if (!digitReg.test(page)) {
            $.toast("页数只能为数字!");
            return;
        }
        showAwardListPage(shopId, getAwardListUrl, $("#search").val(), $("#dump-page-input").val());
    })

    $(document).on("click", ".sold-out", function () {
        let soldOutOrInAwardUrl = "/o2o/award/soldOutOrInAward";
        let awardId = $(this).attr("award-id");
        let enableStatus = $(this).attr("enable-status");
        soldOutOrInAward(enableStatus, soldOutOrInAwardUrl, awardId, shopId);
    })
})

function soldOutOrInAward(enableStatus, soldOutOrInAwardUrl, awardId, shopId) {
    if (enableStatus == 0) {
        var confirmInfo = "确认上架吗？";
        enableStatus = 1;
    } else {
        var confirmInfo = "确认下架吗？";
        enableStatus = 0;
    }
    $.confirm(confirmInfo, function () {

        jqxhr =  $.ajax({
            url: soldOutOrInAwardUrl,
            data: {"awardId": awardId, "shopId": shopId, "enableStatus": enableStatus},
            type: "POST",
            async: true,
            success: function (data) {
                ajaxRedirect(data);
                if (data.success) {
                    if (enableStatus == 0) {
                        $.toast("下架成功");
                    } else{
                        $.toast("上架成功");
                    }
                    showAwardListPage(shopId, "/o2o/award/getAwardList", $("#search").val(), $page);
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
 * @param getAwardListUrl
 */
function showAwardListPage(shopId, getAwardListUrl, awardName, page) {
    if (page != null && page <= 0) {
        $.toast("已经是第一页");
        return;
    }
    //如果page不为null（页面加载完成第一次请求时传的参数为null）说明已经请求过后台了，最大页面全局变量就有值了
    if (page != null && page > $totalPage) {
        $.toast("已经是最后一页");
        return;
    }

    $("#award-list").html("");
    $("#user-name").html("");
    $("#dump-page-input").val("");

    jqxhr = $.ajax({
        url: getAwardListUrl,
        data: {"shopId": shopId, "awardName": awardName, "page": page},
        type: "POST",
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                let awardList = data.pageInfo.list;
                $.each(awardList, function (i, award) {
                    //状态为0，说明是不可用状态，此时需要显示为上架，反之亦然
                    if (award.enableStatus == 0) {
                        var enableStatus = "上架";
                    } else {
                        var enableStatus = "下架"
                    }
                    let awardDiv = $("<div class='row row-shop'></div>")
                        .append($("<div class='col-33'></div>").append(award.awardName))
                        .append($("<div class='col-33'></div>").append(award.point))
                        .append($("<div class='col-33'></div>")
                            .append($("<div class='row'></div>")
                                .append($("<div class='col-33'></div>").append($("<a>编辑</a>").prop("href",
                                    "/o2o/award/awardEditPage?shopId=" + shopId + "&awardId=" + award.awardId)))
                                .append($("<div class='col-33'></div>").append($("<a class='sold-out'>"+enableStatus+"</a>").attr("award-id", award.awardId)
                                    .attr("enable-status", award.enableStatus)))
                                .append($("<div class='col-33'></div>").append($("<a>预览</a>").prop("href", "/o2o/loginFrontEnd/awardListPage?userType=1&shopId="+award.shop.shopId+"&parentId=")))
                            ));
                    $("#award-list").append(awardDiv);
                })
                if (awardList == null || awardList == "") {
                    $("#dump-page").html("没有数据");
                    return;
                }
                $("#user-name").html(data.user.name);
                $page = data.pageInfo.pageNum;
                $("#current-page").html($page);
                $totalPage = data.pageInfo.pages;
            } else {
            }
        },
        dataType: "json"
    })
}
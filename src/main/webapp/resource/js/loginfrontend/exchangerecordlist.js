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
    //点击兑换
    exchangeAward();

    //点击领取奖品
    $(document).on("click", ".card", function() {
        let userAwardMapId = $(this).attr("user-award-map-id");
        let shopId = $(this).attr("shop-id");
        if (userAwardMapId == "") {
            $.toast("该奖品已领取！");
            return;
        } else {
            window.location.href="/o2o/loginFrontEnd/userAwardMapDetailPage?userAwardMapId="+userAwardMapId+"&shopId="+shopId;
        }
    })
})

/**
 * 点击兑换
 */
function exchangeAward() {
    $(document).on("click", ".show-award", function() {
        var awardId = $(this).attr("award-id");
        $.confirm("确认兑换吗？", function() {
            jqxhr =  $.ajax({
               url: "/o2o/loginFrontEnd/exchangeAward",
               data: {"shopId": shopId, "awardId": awardId},
               async: true,
               type: "POST",
               success: function(data) {
                   ajaxRedirect(data);
                   if (data.success) {
                       $.toast("兑换成功！");
                       window.location.reload();
                   } else {
                       $.toast(data.errorMsg);
                   }
               }
           })
        });
    })
}

/**
 * 滑屏幕自动进行分页搜索
 */
function inTheScreen() {
    $(document).on('infinite', '.infinite-scroll-bottom', function () {
        if (loading) {
            return;
        }
        let awardName = $("#search").val();
        addItem(pageInfo.pageNum, awardName);
    });
}

/**
 * 搜索框
 */
function search() {
    $('#search').on('input', function (e) {
        $('.list-div').empty();
        let page = 1;
        let awardName = $("#search").val();
        addItem(page, awardName);
    });
}


/**
 * 分页查询
 */
function addItem(page, awardName){

    $("#bottom").html("");


    let exchangeRecordListUrl = "/o2o/loginFrontEnd/exchangeRecordList";
    //设定加载符，若还在后台取数据则不能再次访问后台
    loading = true;
    //访问后台获取相应查询条件下的店铺列表
    jqxhr = $.ajax({
        url: exchangeRecordListUrl,
        data: {
            "page": page, "awardName": awardName},
        async: false,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);

                pageInfo = data.pageInfo;
                if (pageInfo == null) {
                    $("#user-award-map-list").append("没有数据！");
                    return;
                }
                //获取当前查询条件下店铺的总数
                let maxItems = pageInfo.total;
                //遍历集合中取出来的店铺列表
                var userAwardMapHtml = "";
                $.each(pageInfo.list, function (i, userAwardMap) {
                    if (userAwardMap.usedStatus == 0) {
                        var isGet = "未领取";
                        var userAwardMapId = userAwardMap.userAwardMapId;
                    } else {
                        var isGet = "已领取";
                        var userAwardMapId = "";
                    }
                    userAwardMapHtml +=
                                "<div class='card' shop-id='"+userAwardMap.shop.shopId+"' user-award-map-id='"+userAwardMapId+"'>"+
                                "<div class='card-header'>"+userAwardMap.shop.shopName +
                        "<span class='pull-right'>"+isGet+"</span></div>"+
                                "<div class='card-content'>"+
                                "<div class='list-block media-list'>"+
                                "<ul>"+
                                "<li class='item-content'>"+
                                "<div class='item-media'>"+
                                "<img src='" + userAwardMap.award.awardImg + "' width='44'>" +
                                "</div>"+
                                "<div class='item-inner'>"+
                                "<div class='item-title-row'>"+
                                "<div class='item-title'>"+userAwardMap.award.awardName+"</div>"+
                                "</div>"+
                                "</div>"+
                                "</li>"+
                                "</ul>"+
                                "</div>"+
                                "</div>"+
                                "<div class='card-footer'>"+
                                "<span>"+new Date(userAwardMap.createTime).Format('yyyy-MM-dd hh:mm:ss')+"</span>"+
                                "<span>消耗积分:"+userAwardMap.award.point+"</span>"+
                                "</div>"+
                                "</div>";
                })
                $("#user-award-map-list").append(userAwardMapHtml);
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

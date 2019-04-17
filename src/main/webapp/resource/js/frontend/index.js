//点击显示侧边栏
$("#me").click(function() {
    $.openPanel("#panel-js-demo");
})
$(document).on("click", ".my-btn", function() {
    $.openPanel("#panel-js-demo");
});
$(function() {
    let showFrontEndListUrl = "/o2o/frontEnd/showFrontEndList";
    showFrontEndList(showFrontEndListUrl);

    $("#all-shop").click(function () {
        window.location.href = "/o2o/frontEnd/frontEndShopPage";
    })
    $(document).on("click", ".one-category", function() {
        let parentId = $(this).attr("category-id");
        window.location.href = "/o2o/frontEnd/frontEndShopPage?parentId=" + parentId;
    })
});


/**
 * 初始化首页
 * @param showFrontEndListUrl
 */
function showFrontEndList(showFrontEndListUrl) {
    jqxhr = $.ajax({
        url : showFrontEndListUrl,
        type : "POST",
        async : true,
        success : function (data) {
            if (data.success) {
                showSildeBar(data.user);
                showHeadLine(data.headLineList);
                showCategory(data.shopCategoryList);
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType : "json"
    })
}


/**
 * 动态显示一级类别
 * @param shopCategoryList
 */
function showCategory(shopCategoryList) {
    var oneCategory = "";
    $.each(shopCategoryList, function (i, shopCategory) {
        //因为一行只显示两个，所以奇数循环次数拼装开头标签，偶数循环次数拼装结尾标签
        if(i % 2 == 0) {
            oneCategory += "<li class='item-content' style='background-color : #efeff4'>";
        }

        oneCategory +=
            "<div class='item-media one-category' category-id='"+shopCategory.shopCategoryId+"'>"+
                "<img src='"+shopCategory.shopCategoryImg+"' width='70' height='70'>"+
            "</div>"+
            "<div class='item-inner one-category' category-id='"+shopCategory.shopCategoryId+"'>"+
                "<div class='item-title-row'>"+
                    "<div class='item-title'>"+shopCategory.shopCategoryName+"</div>"+
                    "<span style='font-size: 13px;'>"+shopCategory.shopCategoryDesc+"</span>"+
                 "</div>"+
            "</div>";

        if(i % 2 != 0) {
            oneCategory += "</li>";
        }
    })

    $("#one-category").html(oneCategory);
}

/**
 * 动态显示轮播图
 * @param headLineList
 */
function showHeadLine(headLineList) {
    /**
     * <div class="swiper-slide">
         <img src="\upload\item\headtitle\2017061320393452772.jpg" width="100%" height="30%" alt="轮播图">
     </div>
     */
    $.each(headLineList, function (i, headLine) {
        let headLineDiv = $("<div class='swiper-slide'></div>")
            .append($("<img width='100%' height='30%'>").prop("src", headLine.lineImg).prop("alt", headLine.lineName));
        $("#slide").append(headLineDiv);
    })
    //因为是异步加载的，所以等拼装完成后再添加配置
    //轮播图的效果
    $(".swiper-container").swiper({
        //3秒换一次
        autoplay : 3000,
        //用户对轮播图进行操作时，是否自动停止autoplay
        autoplayDisableOnInteraction : false
    });
}
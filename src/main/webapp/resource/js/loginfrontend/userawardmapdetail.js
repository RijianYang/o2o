$(function() {

    let userAwardMapId = getQueryString("userAwardMapId");
    let shopId = getQueryString("shopId");
    $("#return").prop("href", "/o2o/loginFrontEnd/exchangeRecordListPage?userType=1");

    $('#me').click(function () {
        $.openPanel('#panel-js-demo');
    });
    $.init();

    //获取用户奖品映射信息信息
    getUserAwardMapById(userAwardMapId);

    $("#qrCode").prop("src", "/o2o/loginFrontEnd/generateQRCodeForGetAward?userAwardMapId="+userAwardMapId+"&shopId="+shopId);
})

/**
 * 获取用户奖品映射信息信息
 * @param userAwardMapId
 */
function getUserAwardMapById(userAwardMapId) {
    jqxhr = $.ajax({
        url: "/o2o/loginFrontEnd/getUserAwardMapById",
        data: {"userAwardMapId": userAwardMapId},
        type: "POST",
        async: true,
        success: function(data) {
            ajaxRedirect(data);
            if (data.success) {
                showSildeBar(data.user);
                let userAwardMap = data.userAwardMap;
                $("#award-name").append(userAwardMap.award.awardName);
                $("#user-award-map-create-time").append(new Date(userAwardMap.createTime).Format("yyyy-MM-dd hh:mm:ss"));
                $("#award-img").prop("src", userAwardMap.award.awardImg);
                $("#award-desc").append(userAwardMap.award.awardDesc);
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType: "json"
    })
}
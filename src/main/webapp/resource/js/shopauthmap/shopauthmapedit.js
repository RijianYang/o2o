$(function(){
    //页面加载就去后台获得授权信息进行回显
    shopAuthMapId = getQueryString("shopAuthMapId");
    shopId = getQueryString("shopId");
    $("#return").prop("href", document.referrer);
    getAuthMapById(shopAuthMapId);
})

/**
 * 回显授权信息
 * @param shopAuthMapId
 */
function getAuthMapById(shopAuthMapId) {
    jqxhr =  $.ajax({
        url: "/o2o/shopAuthMap/getShopAuthMapById",
        data: {"shopAuthMapId": shopAuthMapId},
        type: "POST",
        async: true,
        success: function(data) {
            ajaxRedirect(data);
            if (data.success) {
                let shopAuthMap = data.shopAuthMap;
                $("#name").val(shopAuthMap.employee.name);
                $("#title").val(shopAuthMap.title);
            } else {
                $.toast(data.errorMsg);
            }
        }
    })
}
$("#submit").click(function() {
    let verifyCode = $("#j-kaptcha").val();
    let title = $("#title").val();
    if (checkForm(verifyCode, title)) {
        jqxhr = $.ajax({
            url: "/o2o/shopAuthMap/modifyShopAuthMapById",
            data: {"shopAuthMapId": shopAuthMapId, "title": title, "verifyCode": verifyCode, "isCode": true},
            type: "POST",
            async: true,
            success: function(data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("操作成功！");
                    window.location.href = "/o2o/shopAuthMap/shopAuthMapListPage?shopId=" + shopId;
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType: "json"
        })
    }
})

function checkForm(verifyCode, title) {
    if (title == null || title.trim() == "") {
        $.toast("请填写职位！");
        return false;
    }
    if (title.length < 2 || title.length > 6) {
        $.toast("职称为2到6位之间！");
        return false;
    }
    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请填写验证码！");
        return false;
    }
    return true;
}
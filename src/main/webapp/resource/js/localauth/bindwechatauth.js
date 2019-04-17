
$(function(){
    $("#return").prop("href", document.referrer);
})
$("#submit").click(function() {
    //1、获取用户名，密码，验证码，获取是前台还是店铺后台管理
    let username = $("#username").val();
    let password = $("#password").val();
    let verifyCode = $("#j-kaptcha").val();
    let userType = getQueryString("userType");
    //2、进行校验
    // let isPassCheck = checkForm(username, password, verifyCode);

    if (true) {
        //3、提交到后台绑定微信
        bindWechatAuth(username, password, verifyCode, userType);
    }

})

/**
 * 提交到后台绑定微信
 * @param username
 * @param password
 * @param kapatcha
 */
function bindWechatAuth(username, password, verifyCode, userType) {
    jqxhr = $.ajax({
        url : "/o2o/localAuth/bindWechatAuth",
        data : {"username" : username, "password" : password, "verifyCode" : verifyCode},
        type : "POST",
        async : true,
        success : function (data) {
            if (data.success) {
                $.toast("绑定成功！");
                if (userType == 2) {
                    window.location.href = "/o2o/shop/shopListPage";
                } else {
                    window.location.href = "/o2o";
                }
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType : "json"
    })
}

/**
 * 校验表单
 * @param username
 * @param password
 * @param kaptcha
 */
function checkForm(username, password, verifyCode) {
    if (username == null || username.trim() == "") {
        $.toast("请填写用户名！");
        return false;
    }
    if (username.length < 2 || username.length > 10) {
        $.toast("用户名为2-10位之间！");
        return false;
    }

    if (password == null || password.trim() == "") {
        $.toast("请填写密码！");
        return false;
    }
    if (password.length < 5 || password.length > 10) {
        $.toast("密码为5-10位之间！");
        return false;
    }
    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请填写验证码！");
        return false;
    }
    return true;
}
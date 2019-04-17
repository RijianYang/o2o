$(function() {
    isShow = 0;
    $("#submit").click(function () {
        let userType = getQueryString("userType");
        //1、获取用户名密码
        let username = $("#username").val();
        let password = $("#password").val();
        var verifyCode = "";
        if (isShow >= 3) {
            verifyCode = $("#j-kaptcha").val();
        }
        //2、校验表单
        let isPassCheck = checkForm(username, password, verifyCode);
        if (isPassCheck) {
            login(username, password, verifyCode, userType);
        }
    })
})

/**
 * 登录方法
 */
function login(username, password, verifyCode, userType) {
    var flag = false;
    if (isShow >= 3) {
        //如果错误超过了3次后台就会校验字符串
        flag = true;
    }
    jqxhr = $.ajax({
        url: "/o2o/superAdmin/login",
        type: "POST",
        data: {"username": username, "password": password, "verifyCode": verifyCode, "flag": flag, "userType": userType},
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                $.toast("登录成功");
                window.location.href="/o2o/superAdmin/superAdminManagePage"

            } else {
                $.toast(data.errorMsg);
                isShow++;
                if (isShow >= 3) {
                    $("#verify-code-li")[0].removeAttribute("hidden");
                }
            }
        },
        dataType: "json"
    })

}

/**
 * 校验表单
 * @param username
 * @param password
 * @param verifyCode
 */
function checkForm(username, password, verifyCode) {

    if (username == null || username.trim() === "") {
        $.toast("请填写用户名！");
        return false;
    }

    if (password == null || password.trim() === "") {
        $.toast("请填写密码！");
        return false;
    }
    if (isShow >= 3 && (verifyCode == null || verifyCode.trim() === "")) {
        $.toast("请填写验证码！");
        return false;
    }
    return true;
}
$(function () {
    //因为登录页面是点击退出系统而转发过来的
    //所以页面一加载就去后台调用去除Session中的User方法
    exitSystem();
});

/**
 * 退出系统
 */
function exitSystem() {
    $.ajax({
        url: "/o2o/localAuth/exitSystem",
        async: true,
        type: "POST",
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                //退出系统后登录就可以生效了
                //使用一个全局变量作用：只要登录失败了3次就需要输入验证码
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
            }
        },
        dataType: "json"
    })
}

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
        url: "/o2o/localAuth/login",
        type: "POST",
        data: {"username": username, "password": password, "verifyCode": verifyCode, "flag": flag, "userType": userType},
        async: true,
        success: function (data) {
            ajaxRedirect(data);
            if (data.success) {
                $.toast("登录成功");
                var base64URL = getQueryString("url");
                if (base64URL != "") {
                    // var b = new Base64();
                    let reuqestURL = window.atob(base64URL);
                    window.location.href = reuqestURL;
                } else {
                    //如果没有被拦截到也就没有上一次请求的url就根据userType来决定
                    if (userType == 1) {
                        window.location.href = "/o2o";
                    } else {
                        window.location.href = "/o2o/shopManager";
                    }
                }

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
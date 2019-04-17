

/**
 * 点击修改密码按钮
 */
$("#submit").click(function() {
    //1、获取用户名，原密码和旧密码，验证码
    let username = $("#username").val();
    let password = $("#password").val();
    let newPassword = $("#new-password").val();
    let reNewPassword = $("#re-new-password").val();
    let verifyCode = $("#j-kaptcha").val();
    //2、校验
    let isPassCheck = checkForm(username, password, newPassword, reNewPassword, verifyCode);
    //3、提交到后台修改
    if (isPassCheck) {
        modifyPassword(username, password, newPassword, verifyCode);
    }

})

/**
 * 修改密码
 * @param username
 * @param password
 * @param newPassword
 * @param userType
 */
function modifyPassword(username, password, newPassword, verifyCode) {
    jqxhr = $.ajax({
        url: "/o2o/superAdmin/modifyPassword",
        data: {"username": username, "password": password, "newPassword": newPassword, "verifyCode": verifyCode},
        type: "POST",
        async: true,
        success: function(data) {
            ajaxRedirect(data);
            if (data.success) {
                $.toast("修改成功！");
                window.location.href = "/o2o/superAdmin/loginPage";
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType: "json"
    })
}

/**
 * 校验
 * @param username
 * @param password
 * @param newPassword
 * @param reNewPassword
 * @returns {boolean}
 */
function checkForm(username, password, newPassword, reNewPassword, verifyCode) {
    if (username == null || username.trim() == "") {
        $.toast("请填写账号！");
        return false;
    }
    if (password == null || password.trim() == "") {
        $.toast("请填写原码密码！");
        return false;
    }
    if (newPassword == null || newPassword.trim() == "") {
        $.toast("请填写新密码！");
        return false;
    }
    if (newPassword.length < 5 || newPassword.length > 10) {
        $.toast("新密码长度为5-10位之间！");
        return false;
    }
    if (reNewPassword == null || reNewPassword.trim() == "") {
        $.toast("请填写确认密码！");
        return false;
    }
    if (newPassword != reNewPassword) {
        $.toast("两次输入密码不一致！");
        return false;
    }
    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请输入验证码！")
        return false;
    }

    return true;
}
/**
 * 点击一次图片就会请求后台KaptchaServlet重新生成一张验证码
 * @param img
 */
$(function() {

    $("#verifyCode").click(function() {
        this.src = "../kaptcha?" + Math.floor(Math.random() * 100);
    })

});

function ajaxRedirect(data) {
    if (data.isRedirect) {
        window.location.href = data.loginURL;
        return;
    }
}

//格式化时间类型字符串
Date.prototype.Format = function(fmt) {
    var o = {
        "M+" : this.getMonth() + 1, // 月份
        "d+" : this.getDate(), // 日
        "h+" : this.getHours(), // 小时
        "m+" : this.getMinutes(), // 分
        "s+" : this.getSeconds(), // 秒
        "q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
        "S" : this.getMilliseconds()
        // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
            .substr(4 - RegExp.$1.length));
    for ( var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length ===1) ? (o[k])
                : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

/**
 * 获取URL中请求参数的值
 * @param name
 * @returns {string}
 */
function getQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //取地址栏上的URL(以?开头截取)eg:?key=value
    let param = window.location.search;
    //把URL参数取出来然后匹配对应的正则表达式返回一个数组
    //r: (4) ["shopId=1", "", "1", "", index: 0, input: "shopId=1", groups: undefined]
    let r = param.substr(1).match(reg);
    if(r != null) {
        return decodeURIComponent(r[2]);
    }
    return '';
}

/**
 * 睡眠函数
 * @param second
 */
function sleep(second) {
    var now = new Date();
    var exitTime = now.getTime() + second;
    while(true) {
        now = new Date();
        if(now.getTime() >= exitTime) {
            return;
        }
    }
}

/**
 * 根据是否登录显示不同的侧边栏
 * @param user
 */
function showSildeBar (user) {
    var sildeBar = "";
    if (user != null) {
        sildeBar =
            "<div class='content-block'>"+
            "<p>"+
            "<a href='/o2o/localAuth/bindWechatAuthPage?userType=1' class='close-panel'>绑定微信</a>"+

            "</p>"+
            "<p>"+
            "<a href='/o2o/localAuth/loginPage?userType=1' class='close-panel'>退出系统</a>"+
            "</p>"+
            "<p>"+
            "<a href='/o2o/localAuth/modifyPasswordPage?userType=1' class='close-panel'>修改密码</a>"+
            "</p>"+
            "<p>"+
            "<a href='/o2o/loginFrontEnd/userProductMapListPage' class='close-panel'>消费记录</a>"+
            "</p>"+
            "<p>"+
            "<a href='/o2o/loginFrontEnd/userShopMapListPage' class='close-panel'>我的积分</a>"+
            "</p>"+
            "<p>"+
            "<a href='/o2o/loginFrontEnd/exchangeRecordListPage?userType=1' class='close-panel'>兑换记录</a>"+
            "</p>"+
            "<p><a href='#' class='close-panel'>关闭</a></p>"+
            "</div>";
    } else {
        sildeBar ="<div class='content-block'>"+
            "<p>"+
            "<a href='/o2o/localAuth/loginPage?userType=1' class='close-panel'>登录</a>"+
            "</p>"+
            "<p><a href='#' class='close-panel'>关闭</a></p>"+
            "</div>";
    }
    $("#panel-js-demo").html(sildeBar);
}


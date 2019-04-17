/**
 * 注册店铺所需要的js
 */
$(function () {
    let registerShopUrl = "/o2o/shop/registerShop";
    let initUrl = "/o2o/shop/getShopInitInfo";
    let modifyUrl = "/o2o/shop/modifyShop";
    //页面加载完毕就获得参数值
    //下面函数里面会被使用(所以使用全局变量)
    shopId = getQueryString("shopId");
    let echoUrl = "/o2o/shop/echoShop?shopId=" + shopId;

    //如果有shopId就执行修改店铺的逻辑
    //否则就执行添加店铺的逻辑
    if (shopId) {
        echoShop(echoUrl);
    } else {
        getShopInitInfo(initUrl);
    }
    $("#submit").click(function () {
        registerShop(registerShopUrl, modifyUrl);
    });
})

function echoShop(echoUrl) {
    $.getJSON(echoUrl, function (data) {
        if(data.success) {
            let shop = data.shop;
            $("#shop-name").val(shop.shopName);
            $("#shop-addr").val(shop.shopAddr);
            $("#phone").val(shop.phone);
            $("#shop-desc").val(shop.shopDesc);
            data.areaList.map(function (item, index) {
                if(shop.area.areaId == item.areaId){
                    var $areaOp = $("<option></option>").attr("data-id", item.areaId).append(item.areaName);
                    $areaOp.prop("selected", "selected");
                } else {
                    var $areaOp = $("<option></option>").attr("data-id", item.areaId).append(item.areaName);
                }
                $("#area").append($areaOp);
            })
            let $shopCategoryOp = $("<option></option>").attr("data-id", shop.shopCategory.shopCategoryId).append(shop.shopCategory.shopCategoryName);
            $shopCategoryOp.prop("selected", true);
            $("#shop-category").append($shopCategoryOp);
            $("#shop-category").prop("disabled", true);
        }
    })
}

/**
 * 初始化区域下拉列表和店铺分类下拉列表
 */
function getShopInitInfo(initUrl) {
    //param1:ajax请求的url  param2:请求成功后的回调函数
    $.getJSON(initUrl, function (data) {
        if (data.success) {
            data.shopCategoryList.map(function (item, index) {
                let $shopCategoryOp = $("<option></option>").attr("data-id", item.shopCategoryId).append(item.shopCategoryName);
                $("#shop-category").append($shopCategoryOp);
            })
            data.areaList.map(function (item, index) {
                let $areaOp = $("<option></option>").attr("data-id", item.areaId).append(item.areaName);
                $("#area").append($areaOp);
            })
        }
    })
}

/**
 * 注册店铺
 * @param registerShopUrl
 */
function registerShop(registerShopUrl, modifyUrl) {
    let shop = {};
    //封装shop对象
    if(shopId) {
        shop.shopId = shopId;
    }
    shop.shopName = $("#shop-name").val();
    shop.shopAddr = $("#shop-addr").val();
    shop.phone = $("#phone").val();
    shop.shopDesc = $("#shop-desc").val();
    shop.shopCategory = {
        //not(参数) 遍历集合中排除参数内的所有元素
        shopCategoryId: $("#shop-category").find("option").not(function () {
            //函数返回的是一个not函数遍历的条件：这里的意思是遍历所有的option，把所有不被选中的作为他的条件，not函数是把条件作为排除条件，
            // 负负得正，所以这里选择是选中的
            return !this.selected;
        }).attr("data-id")
    };
    shop.area = {
        areaId: $("#area").find("option").not(function () {
            return !this.selected;
        }).attr("data-id")
    };
    var shopImg = $("#shop-img")[0].files[0];
    //获取验证码
    var $verifyCode = $("#j-kaptcha").val();

    //封装表单数据
    var formData = new FormData();
    formData.append("shopImg", shopImg);
    formData.append("shopStr", JSON.stringify(shop));
    formData.append("verifyCode", $verifyCode);
    //提交之前校验数据输入的是否规范
    if (checkForm(formData)) {
        jqxhr = $.ajax({
            //如果有shopId就请求修改Shop的controller
            url: shopId ? modifyUrl : registerShopUrl,
            data: formData,
            type: "POST",
            async: true,
            processData: false,
            contentType: false,
            success: function (data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("提交成功!");
                    //提交成功后重定向到店铺列表页面
                    window.location.href = "/o2o/shop/shopListPage";
                } else {
                    $.toast("提交失败!" + data.errorMsg);
                }
                //不管提交成功还是失败都要重新生成一次验证码
                $("#verifyCode").click();
            },
            dataType: "json"
        });
    }
}

/**
 * 校验注册店铺输入的内容是否规范
 */
function checkForm(formData) {

    //获得表单参数中的所有数据
    let shop = JSON.parse(formData.get("shopStr"));
    let shopImg = formData.get("shopImg");
    let verifyCode = formData.get("verifyCode");

    //校验店铺名称
    if (shop.shopName == null || shop.shopName.trim() == "") {
        $.toast("请填写店铺名称!");
        return false;
    }
    if (shop.shopName.length < 2 || shop.shopName.length > 10) {
        $.toast("店铺名称为2-10位之间!");
        return false;
    }
    //校验店铺分类
    if (shop.shopCategory.shopCategoryId == "no") {
        $.toast("请选择店铺分类!");
        return false;
    }
    //校验店铺区域
    if (shop.area.areaId == "no") {
        $.toast("请选择店铺区域!");
        return false;
    }
    //校验店铺地址
    if (shop.shopAddr == null || shop.shopAddr.trim() == "") {
        $.toast("请填写店铺地址!");
        return false;
    }
    if (shop.shopAddr.length < 2 || shop.shopAddr.length > 15) {
        $.toast("店铺地址为2-15位之间!");
        return false;
    }
    //校验联系电话
    if (shop.phone == null || shop.phone.trim() == "") {
        $.toast("请填写联系电话!");
        return false;
    }
    //正则表达式：只能匹配11位的电话号码
    let regPhone = /^\d{11}$/;
    if (!regPhone.test(shop.phone)) {
        $.toast("号码只能为11位!");
        return false;
    }
    //校验上传文件
    //如果是修改店铺可以不用上传的
    if(!shopId) {
        if (shopImg == "undefined") {
            $.toast("请上传店铺缩略图!");
            return false;
        }
        if (shopImg.type.indexOf("image") == -1) {
            //如果等于-1说明不包含该字符串
            $.toast("请上传正确格式的图片!");
            return false;
        }
    }
    //校验店铺描述
    if (shop.shopDesc == null || shop.shopDesc.trim() == "") {
        $.toast("请输入店铺描述!");
        return false;
    }
    if (shop.shopDesc > 300) {
        $.toast("店铺描述过多!");
        return false;
    }

    //校验验证码
    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请输入验证码!");
        return false;
    }
    //如果上面都通过了就返回true
    return true;
}
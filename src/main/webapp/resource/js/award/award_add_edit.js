
$(function () {
    //页面一加载就把对应的店铺所有的商品分类获取到
    let shopId = getQueryString("shopId");

    $("#back").prop("href", "/o2o/award/awardListPage?shopId=" + shopId);

    let addAwardUrl = "/o2o/award/addAward";
    let modifyAwardUrl = "/o2o/award/modifyAward";
    let echoAwardUrl = "/o2o/award/getAwardById";

    awardId = getQueryString("awardId");
    $("#submit").click(function () {
        addOrModifyAward(addAwardUrl, modifyAwardUrl, shopId);
    })

    //页面一加载就把商品信息回显
    if(awardId){
        echoAward(echoAwardUrl, awardId);
    }
})

/**
 * 回显商品页面
 */
function echoAward(echoAwardUrl, awardId) {
    jqxhr = $.ajax({
        url : echoAwardUrl,
        data : {"awardId" : awardId},
        type : "POST",
        async : true,
        success : function(data) {
          if(data.success) {
              let award = data.award;
              $("#award-name").val(award.awardName);
              $("#priority").val(award.priority);
              $("#award-desc").val(award.awardDesc);
              $("#point").val(award.point);
          } else {
              $.toast(data.errorMsg);
          }
        },
        dataType : "json"
    })
}

/**
 * 添加一个奖品
 * @param addAwardUrl
 * @param shopId
 */
function addOrModifyAward(addAwardUrl, modifyAwardUrl, shopId) {
    var formData = new FormData();
    let awardName = $("#award-name").val();
    let priority = $("#priority").val();
    let awardDesc = $("#award-desc").val();
    let briefImg = $("#briefImg")[0].files[0];
    let point = $("#point").val();
    let verifyCode = $("#j_captcha").val();

    formData.append("awardName", awardName);
    formData.append("priority", priority);
    formData.append("awardDesc", awardDesc);
    formData.append("point", point);
    if(briefImg != "undefined") {
        formData.append("briefImg", briefImg);
    }
    formData.append("briefImg", briefImg);
    formData.append("verifyCode", verifyCode);



    formData.append("shop.shopId", shopId);
    if (awardId) {
        formData.append("awardId", awardId);
    }

    if (checkForm(formData)) {
        jqxhr = $.ajax({
            url: awardId ? modifyAwardUrl : addAwardUrl,
            type: "POST",
            data: formData,
            async: true,
            processData: false,
            contentType: false,
            success: function (data) {
                ajaxRedirect(data);
                if (data.success) {
                    $.toast("操作成功");
                    //操作成功后就跳转到商品列表页面
                    window.location.href = "/o2o/award/awardListPage?shopId=" + shopId;
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType: "json"
        })
    }
}

/**
 * 校验表单数据输入是否规范
 * @param formData
 */
function checkForm(formData) {
    let awardName = formData.get("awardName");
    let awardCategoryId = formData.get("awardCategory.awardCategoryId");
    let priority = formData.get("priority");
    let awardDesc = formData.get("awardDesc");
    let briefImg = formData.get("briefImg");
    let verifyCode = formData.get("verifyCode");
    let point = formData.get("point");

    if (awardName == null || awardName.trim() == "") {
        $.toast("请填写奖品名称");
        return false;
    }
    if (awardName.length < 2 || awardName.length > 10) {
        $.toast("奖品名称为2-10之间");
        return false;
    }


    if (priority == null || priority.trim() == "") {
        $.toast("请填写优先级");
        return false;
    }
    if (priority < 0) {
        $.toast("优先级不能为负数");
        return false;
    }

    if (point == null || point.trim() == "") {
        $.toast("请填写积分");
        return false;
    }
    if (point < 0) {
        $.toast("积分不能为负数");
        return false;
    }


    if (!awardId && briefImg == "undefined") {
        $.toast("请上传缩略图");
        return false;
    }
    if (!awardId && briefImg.type.indexOf("image") == -1) {
        //如果等于-1说明不包含该字符串
        $.toast("请上传正确格式的缩略图!");
        return false;
    }


    if (awardDesc == null || awardDesc.trim() == "") {
        $.toast("请填写奖品描述");
        return false;
    }
    if (awardDesc.length > 300) {
        $.toast("奖品描述最多为300");
        return false;
    }

    if (verifyCode == null || verifyCode.trim() == "") {
        $.toast("请填写验证码");
        return false;
    }
    return true;
}




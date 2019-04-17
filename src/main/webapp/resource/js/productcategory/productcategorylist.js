$(function() {
    shopId = getQueryString("shopId");
    url = "/o2o/productCategory/getProductCategoryListByShopId";
    $("#return").prop("href", "/o2o/shop/shopManagePage?shopId="+shopId);

    //点击新增按钮动态生成添加表框
    $("#add").click(function() {
       createProductCategory();
    });
    $("#submit").click(function() {
        batchSaveProductCategory(shopId);
    })
    //因为列表中的多选框是发送ajax请求到后端读到数据时才创建的，
    //所以直接绑定点击事件无效(绑定时没有多选框),这里使用on方法
    //给document绑定一个监听事件，只要有 .real-delete带有这个Class值的元素出现就会为它绑定param1(click)事件
    $(document).on("click",".real-delete",function(){
        deleteProductCategory($(this).attr("pc-id"))
    })
    $(document).on("click",".en-real-delete",function(){
        $(this).parent().parent().remove();
    })
      sleep(500);
    showProductCategoryListPage(url, shopId);
})

/**
 * 点击删除按钮删除真实的列表数据
 */
function deleteProductCategory(productCategoryId) {
    let isConfirm = $.confirm("确认删除吗？",function() {
        jqxhr = $.ajax({
            url : "/o2o/productCategory/deleteProductCategory",
            type : "POST",
            data : {"productCategoryId" : productCategoryId, "shopId" : shopId},
            async: true,
            success : function (data) {
                if(data.success) {
                    $.toast("删除成功");
                    showProductCategoryListPage(url, shopId);
                } else {
                    $.toast(data.errorMsg);
                }
            },
            dataType : "json"
        })
    });
}



/**
 * 点击提交按钮批量保存商品列表
 */
function batchSaveProductCategory(shopId) {
    var productCategoryList = new Array();
    let $enRealDiv = $(".en-real");
    $.each($enRealDiv, function(i, m) {
        let productCategoryName = $(m).find(".name").val();
        let priority = $(m).find(".priority").val();
        //每个div中的数据就代表一个ProductCategory对象
        let productCategory = {};
        productCategory.productCategoryName = productCategoryName;
        productCategory.priority = priority;
        productCategory.shop = {
            "shopId" : shopId
        };
        productCategoryList[i] = productCategory;
    });
    if(productCategoryList.length == 0) {
        $.toast("至少添加一条数据");
        return;
    }
    jqxhr = $.ajax({
        url : "/o2o/productCategory/batchSaveProductCategory",
        type : "POST",
        data : {"productCategoryListJson" : JSON.stringify(productCategoryList)},
        async: false,
        success : function (data) {
            if(data.success) {
                showProductCategoryListPage(url, shopId);
            } else{
                $.toast(data.errorMsg);
            }

        },
        dataType : "json"
    })
}



/**
 * 点击新增动态生成添加商品类别输入框
 */
function createProductCategory(shopId) {
    var $productCategoryDiv = $(
        $("<div class='row row-shop en-real'></div>")
            .append($("<div class='col-40'></div>").append($("<input class='category-input name' type='text' placeholder='类别名称' />")))
            .append($("<div class='col-40'></div>").append($("<input class='category-input priority' type='number' placeholder='优先级'/> ")))
            .append($("<div class='col-20'></div>")
                .append($("<a class='button button-round en-real-delete'>删除</a>").prop("herf", "#")))
    )
    $("#product-category-list").append($productCategoryDiv);
}

/**
 * 动态渲染productcategorylist.html页面
 * @param url
 */
function showProductCategoryListPage(url, shopId) {
    jqxhr = $.ajax({
        url : url,
        type : "POST",
        data : {"shopId" : shopId},
        async : true,
        success :function(data) {
            if(data.success) {
                showProductCategoryList(data.productCategoryList);
            } else {
                $.toast(data.errorMsg);
            }
        },
        dataType : "json"
    })
}

/**
 * 动态渲染商品类别列表
 * @param list
 */
function showProductCategoryList(list) {
    if(list == null || list == "") {
        $("#product-category-list").html("没有数据");
        return;
    }
    $("#product-category-list").html("");
    $.each(list, function (i, pc){
        var $productCategoryDiv = $(
            $("<div class='row row-shop real'></div>")
                .append($("<div class='col-40'></div>").append(pc.productCategoryName))
                .append($("<div class='col-40'></div>").append(pc.priority))
                .append($("<div class='col-20'></div>")
                    .append($("<a class='button button-round real-delete'>删除</a>").prop("herf", "#").attr("pc-id", pc.productCategoryId)))
        )
        $("#product-category-list").append($productCategoryDiv);
    })
}

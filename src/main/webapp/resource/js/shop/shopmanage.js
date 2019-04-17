$(function(){
    let shopId = getQueryString("shopId");
    $("#shop-info").prop("href", "/o2o/shop/shopOperationPage?shopId=" + shopId);
    $("#category-manage").prop("href", "/o2o/productCategory/productCategoryListPage?shopId=" + shopId);
    $("#product-manage").prop("href", "/o2o/product/productListPage?shopId=" + shopId);
    $("#return").prop("href", "/o2o/shop/shopListPage");
    $("#auth-manage").prop("href", "/o2o/shopAuthMap/shopAuthMapListPage?shopId=" + shopId);
    $("#sale-record").prop("href", "/o2o/productSellDaily/consumeRecordPage?shopId=" + shopId);
    $("#customer-points").prop("href", "/o2o/userShopMap/userShopMapListPage?shopId=" + shopId);
    $("#award-manage").prop("href", "/o2o/award/awardListPage?shopId=" + shopId);
    $("#award-record").prop("href", "/o2o/userAwardMap/userAwardMapListPage?shopId=" + shopId);
})
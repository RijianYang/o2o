package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ProductCategoryStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.ProductCategory;
import com.ahead.service.ProductCategoryService;
import com.ahead.util.JsonUtil;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/24
 */
@RequestMapping("/productCategory")
@Controller
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @RequestMapping("/deleteProductCategory")
    @ResponseBody
    public Map<String, Object> deleteProductCategory(Long productCategoryId, Long shopId) {
        Map<String, Object> modelMap = new HashMap<>();
        if (productCategoryId == null || shopId == null) {
            return ModelMapUtil.errorMsg("非法操作", modelMap);
        }
        try {
            O2oExecution<ProductCategory> productCategoryO2oExecution = productCategoryService.deleteProductCategory(productCategoryId, shopId);
            if (productCategoryO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                return ModelMapUtil.errorMsg(productCategoryO2oExecution.getStateInfo(), modelMap);
            } else {
                modelMap.put("success", true);
            }
            return modelMap;
        } catch (Exception e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
    }

    @RequestMapping("/batchSaveProductCategory")
    @ResponseBody
    public Map<String, Object> batchSaveProductCategory(String productCategoryListJson) {

        Map<String, Object> modelMap = new HashMap<>();
        if (productCategoryListJson == null || "[]".equals(productCategoryListJson.trim())) {
            return ModelMapUtil.errorMsg("至少添加一条类别", modelMap);
        }
        try {
            List<ProductCategory> productCategoryList = JsonUtil.jsonToList(productCategoryListJson, ProductCategory.class);
            if (productCategoryList != null && productCategoryList.size() > 0) {
                for (ProductCategory productCategory : productCategoryList) {
                    if (productCategory.getPriority() == null || productCategory.getProductCategoryName() == null
                            || productCategory.getPriority() <= 0 || "".equals(productCategory.getProductCategoryName().trim())) {
                        return ModelMapUtil.errorMsg("请填写完整信息", modelMap);
                    }
                }
            } else {
                return ModelMapUtil.errorMsg("非法操作", modelMap);
            }

            O2oExecution<ProductCategory> productCategoryO2oExecution = productCategoryService.batchSaveProductCategory(productCategoryList);
            if (productCategoryO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                return ModelMapUtil.errorMsg(productCategoryO2oExecution.getStateInfo(),
                        modelMap);
            } else {
                modelMap.put("success", true);
            }
            return modelMap;
        } catch (Exception e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
    }

    @RequestMapping(value = "/getProductCategoryListByShopId", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getProductCategoryListByShopId(Long shopId) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopId == null) {
            return ModelMapUtil.errorMsg("请求的参数为空", modelMap);
        }
        O2oExecution<ProductCategory> o2oExecution = productCategoryService.getProductCategoryByShopId(shopId);
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        return ModelMapUtil.success("productCategoryList", o2oExecution.getList(), modelMap);
    }

    /**
     * 下面都是页面的转发
     */
    @RequestMapping("/productCategoryListPage")
    public String productCategoryList() {
        return "productcategory/productcategorylist";
    }
}


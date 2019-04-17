package com.ahead.web.controller;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ProductStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.Product;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.service.ProductService;
import com.ahead.util.CodeUtil;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/27
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 上传最大详情图片数
     */
    private static final int MAX_DETAIL_IMAGS = 6;

    @RequestMapping("/soldOutOrInProduct")
    @ResponseBody
    public Map<String, Object> soldOutOrInProduct(Long shopId, Long productId, Integer enableStatus) {
        Map<String, Object> modelMap = new HashMap<>();
        if(shopId == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏", modelMap);
        }
        if(productId == null) {
            return ModelMapUtil.errorMsg("非法操作", modelMap);
        }
        Product product = new Product();
        product.setEnableStatus(enableStatus);
        product.setProductId(productId);
        Shop shop = new Shop();
        shop.setShopId(shopId);
        product.setShop(shop);
        O2oExecution<Product> o2oExecution = productService.soldOutOrInProduct(product);
        if(o2oExecution.getState() != ProductStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        return ModelMapUtil.success(modelMap);
    }


    @RequestMapping("/getProductList")
    @ResponseBody
    public Map<String, Object> getProductList(Integer page, String productName, Long shopId,
                                              HttpServletRequest request){
        Map<String, Object> modelMap = new HashMap<>();
        if(shopId == null || shopId <= 0) {
            return ModelMapUtil.errorMsg("非法修改地址栏,我就不让你得逞!", modelMap);
        }
        if(page == null) {
            page = 1;
        }
        Product product = new Product();
        product.setProductName(productName);
        O2oExecution<Product> o2oExecution = productService.getProductListByWhere(shopId, product, page);
        if(o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        //从Session获取用户
        User u = (User) request.getSession().getAttribute("user");
        //如果最终执行成功就把pageInfo封装进去
        modelMap.put("success", true);
        modelMap.put("pageInfo", o2oExecution.getPageInfo());
        modelMap.put("user", u);
        return modelMap;
    }


    @RequestMapping("/getProductById")
    @ResponseBody
    public Map<String, Object> getProductById(Long productId) {
        Map<String, Object> modelMap = new HashMap<>();
        if(productId != null) {
            O2oExecution<Product> o2oExecution = productService.getProductById(productId);
            if(o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                return  ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
            }
            modelMap.put("success", true);
            modelMap.put("product", o2oExecution.getT());
            return modelMap;
        } else {
            return ModelMapUtil.errorMsg("非法修改地址栏", modelMap);
        }
    }

    /**
     * 修改图片(商品缩略图，多张商品详情图)
     *
     * @param product     商品
     * @param briefImg    缩略图
     * @param detailImgs 多张详情图
     * @return
     */
    @RequestMapping("/modifyProduct")
    @ResponseBody
    public Map<String, Object> modifyProduct(@Validated Product product, BindingResult bindingResult,
                                          @RequestParam(required = false) MultipartFile briefImg,@RequestParam(required = false) MultipartFile[] detailImgs, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();

        if(product.getProductId() == null){
            return ModelMapUtil.errorMsg("非法修改地址栏", modelMap);
        }
        if(!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("校验码输入错误", modelMap);
        }
        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        if (detailImgs.length > MAX_DETAIL_IMAGS) {
            return ModelMapUtil.errorMsg("详情图片最多上传6张", modelMap);
        }
        if(product.getShop().getShopId() == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏,我就不让你得逞!", modelMap);
        }
        if(Double.parseDouble(product.getPromotionPrice()) > Double.parseDouble(product.getNormalPrice())) {
            return ModelMapUtil.errorMsg("折扣价必须小于原价!", modelMap);
        }
        try {
            List<ImgWrap> imgWrapList = new ArrayList<>();
            ImgWrap briefImgWrap = null;
            try {
                //这里不需要对缩略图和详情图进行判断，service层会根据是否为null进行不同的处理
                if(briefImg != null) {
                    briefImgWrap = new ImgWrap(briefImg.getInputStream(), briefImg.getOriginalFilename());
                }
                for (MultipartFile detailImg : detailImgs) {
                    if (!detailImg.getContentType().contains("image")) {
                        return ModelMapUtil.errorMsg("请上传图片类型的详情图", modelMap);
                    }
                    ImgWrap detailImgWrap = new ImgWrap(detailImg.getInputStream(), detailImg.getOriginalFilename());
                    imgWrapList.add(detailImgWrap);
                }
            } catch (IOException e) {
                return ModelMapUtil.ExceptionMsg(e, modelMap);
            }
            //保存商品
            O2oExecution<Product> productO2oExecution = productService.modifyProduct(product, briefImgWrap, imgWrapList);
        } catch (ServiceRuntimeException e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 保存商品(商品缩略图，多张商品详情图)
     *
     * @param product     商品
     * @param briefImg    缩略图
     * @param detailImgs 多张详情图
     * @return
     */
    @RequestMapping("/addProduct")
    @ResponseBody
    public Map<String, Object> addProduct(@Validated Product product, BindingResult bindingResult,
                                          @RequestParam MultipartFile briefImg,@RequestParam MultipartFile[] detailImgs, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();

        if(!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("校验码输入错误", modelMap);
        }
        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        if(product.getProductCategory() == null || product.getProductCategory().getProductCategoryId() == null) {
            return ModelMapUtil.errorMsg("请选择商品分类", modelMap);
        }
        if (!briefImg.getContentType().contains("image")) {
            return ModelMapUtil.errorMsg("请上传图片类型的缩略图", modelMap);
        }
        if (briefImg == null) {
            return ModelMapUtil.errorMsg("请上传缩略图", modelMap);
        }
        if (detailImgs.length <= 0 || detailImgs == null) {
            return ModelMapUtil.errorMsg("详情图片最少上传一张", modelMap);
        }
        if (detailImgs.length > MAX_DETAIL_IMAGS) {
            return ModelMapUtil.errorMsg("详情图片最多上传6张", modelMap);
        }
        if(product.getShop().getShopId() == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏,我就不让你得逞!", modelMap);
        }
        if(Double.parseDouble(product.getPromotionPrice()) > Double.parseDouble(product.getNormalPrice())) {
            return ModelMapUtil.errorMsg("折扣价必须小于原价!", modelMap);
        }
        try {
            List<ImgWrap> imgWrapList = new ArrayList<>();
            ImgWrap briefImgWrap = null;
            try {
                briefImgWrap = new ImgWrap(briefImg.getInputStream(), briefImg.getOriginalFilename());
                for (MultipartFile detailImg : detailImgs) {
                    if (!detailImg.getContentType().contains("image")) {
                        return ModelMapUtil.errorMsg("请上传图片类型的详情图", modelMap);
                    }
                    ImgWrap detailImgWrap = new ImgWrap(detailImg.getInputStream(), detailImg.getOriginalFilename());
                    imgWrapList.add(detailImgWrap);
                }
            } catch (IOException e) {
                return ModelMapUtil.ExceptionMsg(e, modelMap);
            }
            //保存商品
            if (briefImgWrap != null && imgWrapList.size() > 0) {
                O2oExecution<Product> productO2oExecution = productService.addProduct(product, briefImgWrap, imgWrapList);
                if (productO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                    return ModelMapUtil.errorMsg(productO2oExecution.getStateInfo(), modelMap);
                }
            }
        } catch (ServiceRuntimeException e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 下面都是页面的转发
     */
    @RequestMapping("/productAddPage")
    public String productAdd() {
        return "product/productadd";
    }

    @RequestMapping("/productEditPage")
    public String productEdit() {
        return "product/productedit";
    }

    @RequestMapping("/productListPage")
    public String productList() {
        return "product/productlist";
    }
}

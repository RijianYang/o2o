package com.ahead.web.controller;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ProductStateEnum;
import com.ahead.pojo.Award;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.service.AwardService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
@Controller
@RequestMapping("/award")
public class AwardController {

    @Autowired
    private AwardService awardService;

    @RequestMapping("/soldOutOrInAward")
    @ResponseBody
    public Map<String, Object> soldOutOrInAward(Long shopId, Long awardId, Integer enableStatus) {
        Map<String, Object> modelMap = new HashMap<>();

        Award award = new Award();

        award.setEnableStatus(enableStatus);
        award.setAwardId(awardId);
        Shop shop = new Shop();
        shop.setShopId(shopId);
        award.setShop(shop);
        O2oExecution<Award> o2oExecution = awardService.soldOutOrInAward(award);
        if (o2oExecution.getState() != ProductStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        return ModelMapUtil.success(modelMap);
    }

    @RequestMapping("/getAwardList")
    @ResponseBody
    public Map<String, Object> getAwardList(Integer page, String awardName, Long shopId,
                                            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopId == null || shopId <= 0) {
            return ModelMapUtil.errorMsg("非法修改地址栏,我就不让你得逞!", modelMap);
        }
        Award awardWhere = new Award();
        Shop shop = new Shop();
        shop.setShopId(shopId);

        awardWhere.setShop(shop);
        awardWhere.setAwardName(awardName);
        O2oExecution<Award> o2oExecution = awardService.getAwardList(awardWhere, page, 2);
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
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

    @RequestMapping("/getAwardById")
    @ResponseBody
    public Map<String, Object> getAwardById(Long awardId) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<Award> o2oExecution = awardService.getAwardById(awardId);
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        modelMap.put("success", true);
        modelMap.put("award", o2oExecution.getT());
        return modelMap;
    }

    /**
     * @param award    奖品
     * @param briefImg 缩略图
     * @return
     */
    @RequestMapping("/modifyAward")
    @ResponseBody
    public Map<String, Object> modifyAward(@Validated Award award, BindingResult bindingResult,
                                           @RequestParam(required = false) MultipartFile briefImg, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (award.getAwardId() == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏", modelMap);
        }
        if (!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("校验码输入错误", modelMap);
        }
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        try {
            ImgWrap briefImgWrap = null;
            //这里不需要对缩略图和详情图进行判断(因为修改可以不修改图片)，service层会根据是否为null进行不同的处理
            if (briefImg != null) {
                briefImgWrap = new ImgWrap(briefImg.getInputStream(), briefImg.getOriginalFilename());
            }

            //保存商品
            O2oExecution<Award> awardO2oExecution = awardService.modifyAward(award, briefImgWrap);
        } catch (Exception e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 保存商品(商品缩略图，多张商品详情图)
     * @param award    商品
     * @param briefImg 缩略图
     * @return
     */
    @RequestMapping("/addAward")
    @ResponseBody
    public Map<String, Object> addAward(@Validated Award award, BindingResult bindingResult,
                                        @RequestParam MultipartFile briefImg, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();

        if (!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("校验码输入错误", modelMap);
        }
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        if (!briefImg.getContentType().contains("image")) {
            return ModelMapUtil.errorMsg("请上传图片类型的缩略图", modelMap);
        }
        if (briefImg == null) {
            return ModelMapUtil.errorMsg("请上传缩略图", modelMap);
        }
        try {
            ImgWrap briefImgWrap = null;
            briefImgWrap = new ImgWrap(briefImg.getInputStream(), briefImg.getOriginalFilename());
            //保存商品
            if (briefImgWrap != null) {
                O2oExecution<Award> productO2oExecution = awardService.addAward(award, briefImgWrap);
                if (productO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                    return ModelMapUtil.errorMsg(productO2oExecution.getStateInfo(), modelMap);
                }
            }
        } catch (Exception e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 下面都是页面的转发
     */
    @RequestMapping("/awardAddPage")
    public String awardAdd() {
        return "award/awardadd";
    }

    @RequestMapping("/awardEditPage")
    public String awardEdit() {
        return "award/awardedit";
    }

    @RequestMapping("/awardListPage")
    public String awardList() {
        return "award/awardlist";
    }
}

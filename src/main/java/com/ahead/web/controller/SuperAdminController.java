package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.LocalAuth;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.service.LocalAuthService;
import com.ahead.service.ShopService;
import com.ahead.service.UserService;
import com.ahead.util.CodeUtil;
import com.ahead.util.ModelMapUtil;
import com.ahead.util.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/22
 */
@Controller
@RequestMapping("/superAdmin")
public class SuperAdminController {

    @Autowired
    private LocalAuthService localAuthService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserSessionManager userSessionManager;

    @ResponseBody
    @RequestMapping("/operationUser")
    public Map<String, Object> operationUser(User user) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<User> o2oExecution = userService.modifyUserById(user);
        //这里不需要判断，Service中有报错只会抛出异常
        return ModelMapUtil.success(modelMap);
    }

    @RequestMapping("/getUserList")
    @ResponseBody
    public Map<String, Object> getUserList(String name, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();
        User userWhere = new User();
        userWhere.setName(name);
        O2oExecution<User> o2oExecution = userService.getUserList(userWhere, page, 3);
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            return modelMap;
        }
    }

    /**
     * 超级管理员点击了开张或倒闭
     * @param shop
     * @return
     */
    @ResponseBody
    @RequestMapping("/operationShop")
    public Map<String, Object> operationShop(Shop shop) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<Shop> o2oExecution = shopService.modifyShop(shop, null);
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            return ModelMapUtil.success(modelMap);
        }
    }

    /**
     * 获取超级管理员系统中的店铺列表显示
     * @param page
     * @param shopName
     * @return
     */
    @RequestMapping("/getShopList")
    @ResponseBody
    public Map<String, Object> getShopList(Integer page, String shopName) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop shopWhere = new Shop();
        shopWhere.setShopName(shopName);
        O2oExecution<Shop> o2oExecution = shopService.getShopList(shopWhere, page, 4);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            return modelMap;
        }
    }

    /**
     * 超级管理员登录
     * @param request
     * @param localAuth
     * @param bindingResult
     * @param flag
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, @Validated({LocalAuth.Login.class}) LocalAuth localAuth,
                                     BindingResult bindingResult, boolean flag) {
        Map<String, Object> modelMap = new HashMap<>();
        //1、校验用户名密码
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        //2、判断是否需要校验验证码
        if (flag) {
            if (!CodeUtil.checkVerifyCode(request)) {
                return ModelMapUtil.errorMsg("验证码输入错误！", modelMap);
            }
        }
        //3、不需要验证码的话就直接进行登录
        O2oExecution<LocalAuth> o2oExecution = localAuthService.findLocalAuthByUserNameAndPassword(localAuth);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            //如果返回空说明用户名或密码错误
            return ModelMapUtil.errorMsg("用户名或密码错误！", modelMap);
        }
        //否则登录成功
        //登录成功就根据该平台账号属于哪个用户然后放到Session中
        LocalAuth l = o2oExecution.getT();
        //4、如果是该类型不是超级管理员就报错
        if (l.getUser().getUserType() != 0) {
            return ModelMapUtil.errorMsg("该账号不是超级管理员账号！", modelMap);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", l.getUser());
        //处理多处同时登录该用户的情况
        userSessionManager.handleMoreUserLogin(l.getUser().getUserId() + "", session.getId());

        return ModelMapUtil.success(modelMap);
    }

    /**
     * 修改超级管理员密码
     * @param request
     * @param localAuth
     * @param bindingResult
     * @param newPassword
     * @return
     */
    @RequestMapping("/modifyPassword")
    @ResponseBody
    public Map<String, Object> modifyPassword(HttpServletRequest request, @Validated({LocalAuth.Login.class}) LocalAuth localAuth,
                                              BindingResult bindingResult, String newPassword) {
        Map<String, Object> modelMap = new HashMap<>();
        //1、校验原密码是否规范
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }
        //2、校验新密码是否规范
        if (newPassword == null || Objects.equals("", newPassword)) {
            return ModelMapUtil.errorMsg("新密码不能为空！", modelMap);
        }
        if (newPassword.length() < 5 || newPassword.length() > 10) {
            return ModelMapUtil.errorMsg("新密码长度为5-10位之间！", modelMap);
        }
        //3、校验验证码
        if (!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("验证码输入错误！", modelMap);
        }
        User user = (User) request.getSession().getAttribute("user");
        //4、修改密码前先判断当前账号和密码是否正确
        O2oExecution<LocalAuth> o = localAuthService.findLocalAuthByUserNameAndPassword(localAuth);
        if (o.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg("账号或密码输入错误！", modelMap);
        }
        if (o.getState() == CommonStateEnum.SUCCESS.getState()) {
            //如果成功的话再判断当前的用户是否和当前已经登录的用户是否一致
            if (!user.getUserId().equals( o.getT().getUser().getUserId() )) {
                return ModelMapUtil.errorMsg("当前修改的不是你的账号！无效！", modelMap);
            }
        }
        //前面都通过就能修改密码
        O2oExecution<LocalAuth> o2oExecution = localAuthService.modifyLocalAuthPassword(localAuth, newPassword, user.getUserId());
        return ModelMapUtil.success(modelMap);
    }

    /**
     * 转发到超级管理员登录页面
     * @return
     */
    @RequestMapping("/loginPage")
    public String loginPage() {
        return "superadmin/login";
    }

    /**
     * 转发到超级管理员管理页面
     * @return
     */
    @RequestMapping("/superAdminManagePage")
    public String superAdminManagePage() {
        return "superadmin/superadminmanage";
    }

    /**
     * 转发到超级管理员店铺管理列表页面
     * @return
     */
    @RequestMapping("/shopListPage")
    public String shopListPage() {
        return "superadmin/shoplist";
    }

    /**
     * 转发到超级管理员账号管理列表页面
     * @return
     */
    @RequestMapping("/localAuthListPage")
    public String localAuthListPage() {
        return "superadmin/localauthlist";
    }

    /**
     * 转发到用户列表页面
     * @return
     */
    @RequestMapping("/userListPage")
    public String userListPage() {
        return "superadmin/userlist";
    }

    @RequestMapping("/modifyPasswordPage")
    public String modifyPasswordPage() {
        return "superadmin/modifypassword";
    }
}

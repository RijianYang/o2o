package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.LocalAuth;
import com.ahead.pojo.User;
import com.ahead.service.LocalAuthService;
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
 * @time 2019/3/3
 */
@Controller
@RequestMapping("/localAuth")
public class LocalAuthController {

    @Autowired
    private LocalAuthService localAuthService;
    @Autowired
    private UserSessionManager userSessionManager;

    @RequestMapping("/bindWechatAuth")
    @ResponseBody
    public Map<String, Object> bindWechatAuth(HttpServletRequest request, @Validated({LocalAuth.BindWechatAuth.class}) LocalAuth localAuth, BindingResult bindingResult) {
        Map<String, Object> modelMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ModelMapUtil.errorMsg(fieldErrors.get(0).getDefaultMessage(), modelMap);
        }

        if (!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("验证码输入错误！", modelMap);
        }

        //先从Session中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        O2oExecution<LocalAuth> o2oExecution = localAuthService.addLocalAuth(localAuth, user.getUserId());
        if (o2oExecution == null) {
            return ModelMapUtil.errorMsg("该用户名已被使用！", modelMap);
        }
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg("该微信已被绑定！", modelMap);
        }
        //否则就绑定成功
        return ModelMapUtil.success(modelMap);
    }

    @RequestMapping("/exitSystem")
    @ResponseBody
    public Map<String, Object> exitSystem(HttpSession session) {
        Map<String, Object> modelMap = new HashMap<>();
        //退出系统就是把Session中的用户设置为null
        session.setAttribute("user", null);
        return ModelMapUtil.success(modelMap);
    }

    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest request, @Validated({LocalAuth.Login.class}) LocalAuth localAuth,
                                     BindingResult bindingResult, boolean flag, Integer userType) {
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
        //4、如果是店铺管理后台进行登录，且你登录的用户类型不是店铺管理员就返回错误
        //注意：如果是前台首页登录，店铺管理员也是可以登录的
        if (userType == 2 && l.getUser().getUserType() != 2) {
            return ModelMapUtil.errorMsg("该账号不是店铺管理员账号！", modelMap);
        }
        if (l.getUser().getEnableStatus() == 0) {
            return ModelMapUtil.errorMsg("该账号已被封！请联系超级管理员解封！", modelMap);
        }
        HttpSession session = request.getSession();
        session.setAttribute("user", l.getUser());
        //处理多处同时登录该用户的情况
        userSessionManager.handleMoreUserLogin(l.getUser().getUserId() + "", session.getId());
        return ModelMapUtil.success(modelMap);
    }

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
            if (!user.getUserId().equals(o.getT().getUser().getUserId())) {
                return ModelMapUtil.errorMsg("当前修改的不是你的账号！无效！", modelMap);
            }
        }
        //前面都通过就能修改密码
        O2oExecution<LocalAuth> o2oExecution = localAuthService.modifyLocalAuthPassword(localAuth, newPassword, user.getUserId());
        return ModelMapUtil.success(modelMap);
    }

    /**
     * 下面都是页面的转发
     */

    @RequestMapping("/bindWechatAuthPage")
    public String bindWechatAuth() {
        return "localauth/bindwechatauth";
    }

    @RequestMapping("/loginPage")
    public String login() {
        return "localauth/login";
    }

    @RequestMapping("/modifyPasswordPage")
    public String modifyPassword() {
        return "localauth/modifypassword";
    }

}



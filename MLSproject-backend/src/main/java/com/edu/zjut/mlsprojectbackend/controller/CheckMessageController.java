package com.edu.zjut.mlsprojectbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.dto.CheckMessage;
import com.edu.zjut.mlsprojectbackend.service.CheckMessageService;
import com.edu.zjut.mlsprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 用于管理员用户返回审核信息操作
 */
@Controller
@RequestMapping("/api/auth")
public class CheckMessageController {
    @Resource
    CheckMessageService service;
    @Resource
    JwtUtils utils;
    /**
     * 用户获得所有审核信息
     * @return 用户所有的审核信息
     */
    @GetMapping("/getUMessage")
    @ResponseBody
    public  RestBean<List<CheckMessage>> getUMessage(HttpServletRequest request){
        DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
        String userName = utils.toUser(jwt).getUsername();
        List<CheckMessage> list = service.getMessageByUname(userName);
        return RestBean.success(list);
    }
    /**
     * 管理员获得所有审核信息
     * @return 管理员所有的审核信息
     */
    @GetMapping("/getMMessage")
    @ResponseBody
    public  RestBean<List<CheckMessage>> getMMessage(HttpServletRequest request){
        DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
        Integer MId = utils.toId(jwt);
        List<CheckMessage> list = service.getMessageByMId(MId);
        return RestBean.success(list);
    }
    /**
     * 删除指定审核信息
     * @return 是否成功删除
     */
    @PostMapping("/deleteMessage")
    public RestBean<Void> delete(@RequestBody Map<String, Integer> requestBody) {
        Integer id = requestBody.get("id");
        return this.messageHandle(() ->
                service.deleteMessage(id));
    }
    private RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}

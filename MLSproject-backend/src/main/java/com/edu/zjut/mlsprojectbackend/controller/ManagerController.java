package com.edu.zjut.mlsprojectbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import com.edu.zjut.mlsprojectbackend.service.AccountService;
import com.edu.zjut.mlsprojectbackend.service.UserFileService;
import com.edu.zjut.mlsprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 用于管理员用户操作
 */
@RestController
@RequestMapping("/api/auth/manager")
public class ManagerController {
    @Resource
    AccountService accountService;
    @Resource
    UserFileService userFileService;
    @Resource
    JwtUtils utils;

    /**
     * 管理员根据用户名获得用户信息
     * @param accountName 用户名
     * @return 所有用户账号信息
     */
    @GetMapping("/accounts")
    @ResponseBody
    public RestBean<List<Account>> getAccounts(@RequestParam String accountName) {
        List<Account> list = accountService.getAllAccountByName(accountName);
        return RestBean.success(list);
    }
    /**
     * 管理员注销用户
     * @param requestBody 获得前端对象
     * @return 删除结果
     */
    @PostMapping("/deleteAccount")
    public RestBean<Void> delete(@RequestBody Map<String, Integer> requestBody) {
        Integer id = requestBody.get("id");
        return this.messageHandle(() ->
                accountService.deleteAccount(id));
    }
    /**
     * 管理员更新权限
     * @param requestBody 获得前端对象
     * @return 更新结果
     */
    @PostMapping("/updateRole")
    public RestBean<Void> updateRole(@RequestBody Map<String, Object> requestBody){
        String role = (String) requestBody.get("role");
        Integer id = (Integer) requestBody.get("id");
        return this.messageHandle(() ->
                accountService.updateUserRole(id, role));
    }
    /**
     * 管理员获得所有文件
     * @param fileName 文件名
     * @return 所有文件列表
     */
    @GetMapping("/allFiles")
    @ResponseBody
    public RestBean<List<UserFile>> getAllFile(@RequestParam String fileName) {
        List<UserFile> list = userFileService.getAllFilesByName(fileName);
        return RestBean.success(list);
    }
    /**
     * 管理员获得所有未审核文件
     * @param fileName 文件名
     * @return 所有未审核文件列表
     */
    @GetMapping("/uncheckedFiles")
    @ResponseBody
    public RestBean<List<UserFile>> getUncheckedFile(@RequestParam String fileName) {
        List<UserFile> list = userFileService.getAllUncheckedFiles(fileName);
        return RestBean.success(list);
    }
    /**
     * 管理员更新权限
     * @param requestBody 更新审核信息
     * @return 审核结果
     */
    @PostMapping("/checkFile")
    public RestBean<Void> checkFile(@RequestBody Map<String, Object> requestBody,
                                    HttpServletRequest request){
        DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
        String userName = (String) requestBody.get("Uname");
        String check = (String) requestBody.get("check");
        Integer fileId = (Integer) requestBody.get("id");
        Integer MId = utils.toId(jwt);
        String message = (String) requestBody.get("message");
        return this.messageHandle(() ->
                userFileService.checkFile(fileId, check, MId, userName, message));
    }
    private RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}

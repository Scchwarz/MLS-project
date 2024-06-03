package com.edu.zjut.mlsprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.zjut.mlsprojectbackend.entity.dto.CheckMessage;

import java.util.List;

public interface CheckMessageService extends IService<CheckMessage> {
    void createMessage(Integer MId, String Uname, Integer FId, String checked, String message);
    String deleteMessage(Integer id);
    List<CheckMessage> getMessageByMId(Integer MId);
    List<CheckMessage> getMessageByUname(String Uname);
}

package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.zjut.mlsprojectbackend.entity.dto.CheckMessage;
import com.edu.zjut.mlsprojectbackend.mapper.CheckMessageMapper;
import com.edu.zjut.mlsprojectbackend.service.CheckMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检查消息服务实现类。
 */
@Service
public class CheckMessageServiceImpl extends ServiceImpl<CheckMessageMapper, CheckMessage> implements CheckMessageService {

    /**
     * 创建消息记录。
     *
     * @param MId     消息ID
     * @param Uname   用户名
     * @param FId     文件ID
     * @param checked 是否已检查
     * @param message 消息内容
     */
    @Override
    public void createMessage(Integer MId, String Uname, Integer FId, String checked, String message) {
        CheckMessage checkMessage = new CheckMessage(null, MId, Uname, FId, message, checked);
        if(!this.save(checkMessage)){
        }
    }
    /**
     * 删除消息记录。
     *
     * @param id 记录ID
     * @return 如果销毁记录失败，则返回错误信息；否则返回null。
     */
    @Override
    public String deleteMessage(Integer id) {
        if(!this.removeById(id)){
            return "销毁记录失败";
        }
        return null;
    }
    /**
     * 根据消息ID获取消息列表。
     *
     * @param MId 消息ID
     * @return 消息列表
     */
    @Override
    public List<CheckMessage> getMessageByMId(Integer MId) {
        QueryWrapper<CheckMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m_id", MId);
        return this.baseMapper.selectList(queryWrapper);
    }
    /**
     * 根据用户名获取消息列表。
     *
     * @param Uname 用户名
     * @return 消息列表
     */
    @Override
    public List<CheckMessage> getMessageByUname(String Uname) {
        QueryWrapper<CheckMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u_name", Uname);
        return this.baseMapper.selectList(queryWrapper);
    }
}

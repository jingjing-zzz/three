package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskPageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOATaskDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOATaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class BpmOATaskServiceImpl implements BpmOATaskService {

    @Resource
    private BpmOATaskMapper taskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(Long userId, BpmOATaskCreateReqVO createReqVO) {
        BpmOATaskDO task = BeanUtils.toBean(createReqVO, BpmOATaskDO.class)
                .setUserId(userId).setStatus(0);
        if (task.getPriority() == null) {
            task.setPriority(0);
        }
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(Long id, BpmOATaskDO updateReqVO) {
        taskMapper.updateById(updateReqVO.setId(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }

    @Override
    public BpmOATaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOATaskDO> getTaskPage(Long userId, BpmOATaskPageReqVO pageReqVO) {
        return taskMapper.selectPage(userId, pageReqVO);
    }

}
package com.meession.etm.module.bpm.service.oa;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAScheduleCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOASchedulePageReqVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAScheduleDO;
import com.meession.etm.module.bpm.dal.mysql.oa.BpmOAScheduleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class BpmOAScheduleServiceImpl implements BpmOAScheduleService {

    @Resource
    private BpmOAScheduleMapper scheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(Long userId, BpmOAScheduleCreateReqVO createReqVO) {
        BpmOAScheduleDO schedule = BeanUtils.toBean(createReqVO, BpmOAScheduleDO.class)
                .setUserId(userId).setStatus(0);
        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Long id, BpmOAScheduleDO updateReqVO) {
        scheduleMapper.updateById(updateReqVO.setId(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        scheduleMapper.deleteById(id);
    }

    @Override
    public BpmOAScheduleDO getSchedule(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public PageResult<BpmOAScheduleDO> getSchedulePage(Long userId, BpmOASchedulePageReqVO pageReqVO) {
        return scheduleMapper.selectPage(userId, pageReqVO);
    }

}
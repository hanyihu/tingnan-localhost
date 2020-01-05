package com.ruoyi.quartz.service.impl;

import java.util.List;
import javax.annotation.PostConstruct;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quartz.domain.*;
import com.ruoyi.quartz.task.RyTask;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;

/**
 * 定时任务调度信息 服务层
 * 
 * @author ruoyi
 */
@Service
public class SysJobServiceImpl implements ISysJobService
{
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobMapper jobMapper;

    protected final Logger logger = LoggerFactory.getLogger(SysJobServiceImpl.class);
    /**
     * 项目启动时，初始化定时器 
     * 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException
    {
        List<SysJob> jobList = jobMapper.selectJobAll();
        for (SysJob job : jobList)
        {
            updateSchedulerJob(job, job.getJobGroup());
        }
    }

    /**
     * 获取quartz调度器的计划任务列表
     * 
     * @param job 调度信息
     * @return
     */
    @Override
    public List<SysJob> selectJobList(SysJob job)
    {
        return jobMapper.selectJobList(job);
    }

    /**
     * 通过调度任务ID查询调度信息
     * 
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public SysJob selectJobById(Long jobId)
    {
        return jobMapper.selectJobById(jobId);
    }

    /**
     * 暂停任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public int pauseJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0)
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 恢复任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public int resumeJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateJob(job);
        if (rows > 0)
        {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public int deleteJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = jobMapper.deleteJobById(jobId);
        if (rows > 0)
        {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 批量删除调度信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public void deleteJobByIds(String ids) throws SchedulerException
    {
        Long[] jobIds = Convert.toLongArray(ids);
        for (Long jobId : jobIds)
        {
            SysJob job = jobMapper.selectJobById(jobId);
            deleteJob(job);
        }
    }

    /**
     * 任务调度状态修改
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public int changeStatus(SysJob job) throws SchedulerException
    {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status))
        {
            rows = resumeJob(job);
        }
        else if (ScheduleConstants.Status.PAUSE.getValue().equals(status))
        {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public void run(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob properties = selectJobById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 新增任务
     * 
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional
    public int insertJob(SysJob job) throws SchedulerException, TaskException
    {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.insertJob(job);
        if (rows > 0)
        {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
        return rows;
    }

    /**
     * 更新任务的时间表达式
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional
    public int updateJob(SysJob job) throws SchedulerException, TaskException
    {
        SysJob properties = selectJobById(job.getJobId());
        int rows = jobMapper.updateJob(job);
        if (rows > 0)
        {
            updateSchedulerJob(job, properties.getJobGroup());
        }
        return rows;
    }

    /**
     * 更新任务
     * 
     * @param job 任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException
    {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey))
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 校验cron表达式是否有效
     * 
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression)
    {
        return CronUtils.isValid(cronExpression);
    }



    /**
     * 功能描述: <br>  每隔2s进行LiveData实时数据同步
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 11:52
     */
    @Override
    public void dataSyncLiveData() {
        //查询亭南数据ku
        //查询亭南数据ku中实时数据条数
        int counts = jobMapper.getLiveDataCountsByTingNan();

        List<LiveData> liveData= jobMapper.getLiveDataByTingNan();
        logger.info("LiveData 的数量count===={}===={}",counts,liveData.size());
        if(liveData.size()!=counts){
            //清空阿里云中tagInfo数据表
            jobMapper.deleteLiveDataByAliYun();

            //吧亭南数据同步到阿里云中
            jobMapper.insertLiveDataByTingNan(liveData);

            //修改数量
            jobMapper.updateLiveDataCount(liveData.size(),counts);
        }

    }


    /**
     * 功能描述: <br>  每天同步一次TagInfo数据库
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 9:09
     */
    @Override
    public void dataSyncTagInfo() {

        //查询亭南数据ku
       List<TagInfo> tagInfo= jobMapper.getTagInfoByTingNan();
       if(tagInfo.size()!=0){
           //清空阿里云中tagInfo数据表
           jobMapper.deleteTagInfoByAliYun();

           //吧亭南数据同步到阿里云中
           //jobMapper.insertDataByTingNan(tagInfo);
           for (TagInfo tagInfo1 : tagInfo) {
               jobMapper.insertTagInfoData(tagInfo1);
           }

       }


    }


    /**
     * 功能描述: <br>  每天同步一次TAlarmHis数据库
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 9:09
     */
    @Override
    public void dataSyncAlarmHis() {

        //查询亭南数据ku
        List<AlarmHis> alarmHis= jobMapper.getAlarmHisByTingNan();
        if(alarmHis.size()!=0){
            //清空阿里云中tagInfo数据表
            jobMapper.deleteAlarmHisByAliYun();

            //吧亭南数据同步到阿里云中
            jobMapper.insertAlarmHisByTingNan(alarmHis);
        }
    }


    /**
     * 功能描述: <br>  每2s同步一次AlarmReal数据库
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 9:09
     */
    @Override
    public void dataSyncAlarmReal() {

        List<AlarmReal> alarmReals= jobMapper.getAlarmRealByTingNan();

        if(alarmReals.size()!=0){
            //清空阿里云中tagInfo数据表
            jobMapper.deleteAlarmRealByAliYun();

            //吧亭南数据同步到阿里云中
            jobMapper.insertAlarmRealDataByTingNan(alarmReals);
        }
    }


    /**
     * 功能描述: <br>  每隔2s进行一次InfoEvent查询，有新数据进行同步
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/24 14:57
     */
    @Override
    public void dataSyncInfoEvent() {

        //查询info_event_count数量
        int count = jobMapper.getInfoEventCount();

        //查询亭南info_event数据数量
        int countByTingNan = jobMapper.getInfoEventCountByTingNan();

        //判断数量是否相等 不相等则插入数据到阿里云中
        if(count != countByTingNan){

          List<InfoEvent> infoEventList = jobMapper.getInfoEventByTingNan();

          //清空阿里云中info_event的数据
            jobMapper.deleteInfoEventToALiYun();

          if(infoEventList.size()!=0){
              for (InfoEvent infoEvent: infoEventList) {
                  jobMapper.insertInfoEventToALiYun(infoEvent);

              }
             //更新info_event_count数量
              jobMapper.updateInfoEventCount(countByTingNan,count);
          }

        }


    }


}
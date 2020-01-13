package com.ruoyi.quartz.mapper;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.quartz.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 调度任务信息 数据层
 * 
 * @author ruoyi
 */
public interface SysJobMapper
{
    /**
     * 查询调度任务日志集合
     * 
     * @param job 调度信息
     * @return 操作日志集合
     */
    public List<SysJob> selectJobList(SysJob job);

    /**
     * 查询所有调度任务
     * 
     * @return 调度任务列表
     */
    public List<SysJob> selectJobAll();

    /**
     * 通过调度ID查询调度任务信息
     * 
     * @param jobId 调度ID
     * @return 角色对象信息
     */
    public SysJob selectJobById(Long jobId);

    /**
     * 通过调度ID删除调度任务信息
     * 
     * @param jobId 调度ID
     * @return 结果
     */
    public int deleteJobById(Long jobId);

    /**
     * 批量删除调度任务信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobByIds(Long[] ids);

    /**
     * 修改调度任务信息
     * 
     * @param job 调度任务信息
     * @return 结果
     */
    public int updateJob(SysJob job);

    /**
     * 新增调度任务信息
     * 
     * @param job 调度任务信息
     * @return 结果
     */
    public int insertJob(SysJob job);




    @DataSource(value = DataSourceType.SLAVE)
    List<TagInfo> getTagInfoByTingNan();

    @DataSource(value = DataSourceType.SECOND)
    void insertDataByTingNan(@Param("tagInfos") List<TagInfo> tagInfos);

    @DataSource(value = DataSourceType.SECOND)
    void deleteTagInfoByAliYun();

    @DataSource(value = DataSourceType.SLAVE)
    List<LiveData> getLiveDataByTingNan();

    @DataSource(value = DataSourceType.SECOND)
    void deleteLiveDataByAliYun();

    @DataSource(value = DataSourceType.SECOND)
    void insertLiveDataByTingNan(@Param("liveDatas") List<LiveData> liveDatas);

    @DataSource(value = DataSourceType.SLAVE)
    List<AlarmHis> getAlarmHisByTingNan();

    @DataSource(value = DataSourceType.SECOND)
    void deleteAlarmHisByAliYun();

    @DataSource(value = DataSourceType.SECOND)
    void insertAlarmHisByTingNan(@Param("alarmHiss")List<AlarmHis> alarmHiss);

    @DataSource(value = DataSourceType.SLAVE)
    List<AlarmReal> getAlarmRealByTingNan();

    @DataSource(value = DataSourceType.SECOND)
    void deleteAlarmRealByAliYun();

    @DataSource(value = DataSourceType.SECOND)
    void insertAlarmRealDataByTingNan(@Param("alarmReals")List<AlarmReal> alarmReals);

    @DataSource(value = DataSourceType.SECOND)
    void insertTagInfoData(TagInfo tagInfo1);

    @DataSource(value = DataSourceType.SLAVE)
    int getInfoEventCount();

    @DataSource(value = DataSourceType.SLAVE)
    int getInfoEventCountByTingNan();

    @DataSource(value = DataSourceType.SLAVE)
    List<InfoEvent> getInfoEventByTingNan();

    @DataSource(value = DataSourceType.SECOND)
    void insertInfoEventToALiYun(InfoEvent infoEvent);

    @DataSource(value = DataSourceType.SLAVE)
    void updateInfoEventCount(@Param("counts") int counts,@Param("count") int count);

    @DataSource(value = DataSourceType.SECOND)
    void deleteInfoEventToALiYun();

    @DataSource(value = DataSourceType.SLAVE)
    int getLiveDataCountsByTingNan();

    @DataSource(value = DataSourceType.SLAVE)
    void updateLiveDataCount(@Param("size")int size, @Param("count")int count);

    @DataSource(value = DataSourceType.SECOND)
    int updateLiveDataToAliYun(List<LiveData> list);
}

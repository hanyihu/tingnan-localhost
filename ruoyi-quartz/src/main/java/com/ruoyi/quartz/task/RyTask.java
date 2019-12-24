package com.ruoyi.quartz.task;

import com.ruoyi.quartz.service.ISysJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("ryTask")
public class RyTask
{

    @Autowired
    private ISysJobService jobService;

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("执行无参方法");
    }





    /**
     * 功能描述: <br> 每两秒进行LiveData数据同步操作
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/19 9:46
     */
    public void dataSyncLiveData(){
        jobService.dataSyncLiveData();

    }


    /**
     * 功能描述: <br> 每天进行一次TagInfo数据同步
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 11:44
     */
    public void  dataSyncTagInfo(){
       jobService.dataSyncTagInfo();
    }


    /**
     * 功能描述: <br>  每天进行一次历史告警数据AlarmHis同步
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 11:57
     */
    public void dataSyncAlarmHis(){
        jobService.dataSyncAlarmHis();
    }

    /**
     * 功能描述: <br>  每2s进行一次实时告警数据AlarmReal同步
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 11:57
     */
    public void dataSyncAlarmReal(){
        jobService.dataSyncAlarmReal();
    }



    /**
     * 功能描述: <br>  每2s进行一次告警数据InfoEvent查询
     * 〈〉
     * @Param: []
     * @Return: void
     * @Author: 韩以虎
     * @Date: 2019/12/22 11:57
     */
    public void dataSyncInfoEvent(){jobService.dataSyncInfoEvent();}
}

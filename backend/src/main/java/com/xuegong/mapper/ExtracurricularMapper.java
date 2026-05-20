package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExtracurricularMapper {

    /**
     * 按类别统计活动数量
     */
    List<Map<String, Object>> countByCategory();

    /**
     * 按学生汇总积分（Top10）
     */
    List<Map<String, Object>> sumPointsByStudentTop10();

    /**
     * 按状态统计活动数量
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 统计总积分
     */
    Map<String, Object> sumTotalPoints();

    /**
     * 按月统计活动参与趋势
     */
    List<Map<String, Object>> countByMonth();
}

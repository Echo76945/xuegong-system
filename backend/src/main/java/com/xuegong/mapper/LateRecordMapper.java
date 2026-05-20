package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LateRecordMapper {

    /**
     * 按班级统计晚归次数
     */
    List<Map<String, Object>> countByClassName();

    /**
     * 按日期统计晚归次数（最近30天）
     */
    List<Map<String, Object>> countByDate();

    /**
     * 统计异常晚归次数
     */
    long countAbnormal();

    /**
     * 按学生统计晚归次数（Top10）
     */
    List<Map<String, Object>> countByStudentTop10();

    /**
     * 按月统计晚归趋势
     */
    List<Map<String, Object>> countByMonth();
}

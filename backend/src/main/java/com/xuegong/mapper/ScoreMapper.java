package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper {

    /**
     * 按课程统计平均分
     */
    List<Map<String, Object>> avgScoreByCourse();

    /**
     * 按学生统计平均分（GPA）
     */
    List<Map<String, Object>> avgScoreByStudent();

    /**
     * 按学期统计平均分
     */
    List<Map<String, Object>> avgScoreBySemester();

    /**
     * 获取学生成绩排名
     */
    List<Map<String, Object>> getStudentRanking(@Param("semester") String semester);

    /**
     * 按课程统计及格率
     */
    List<Map<String, Object>> passRateByCourse();

    /**
     * 统计成绩分布（90+、80-89、70-79、60-69、<60）
     */
    List<Map<String, Object>> scoreDistribution();
}

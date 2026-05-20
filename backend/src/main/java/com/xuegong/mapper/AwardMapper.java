package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AwardMapper {

    /**
     * 按级别统计奖项数量
     */
    List<Map<String, Object>> countByLevel();

    /**
     * 按类型统计奖项数量
     */
    List<Map<String, Object>> countByType();

    /**
     * 按学生统计获奖次数（Top10）
     */
    List<Map<String, Object>> countByStudentTop10();

    /**
     * 统计总奖金
     */
    Map<String, Object> sumTotalBonus();

    /**
     * 按月统计获奖趋势
     */
    List<Map<String, Object>> countByMonth();
}

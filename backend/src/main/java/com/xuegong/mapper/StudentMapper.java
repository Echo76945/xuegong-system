package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    /**
     * 按班级统计学生人数
     */
    List<Map<String, Object>> countByClassName();

    /**
     * 按状态统计学生人数
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 获取班级列表
     */
    List<String> findAllClassNames();

    /**
     * 按性别统计学生人数
     */
    List<Map<String, Object>> countByGender();

    /**
     * 统计学生总数
     */
    long countAll();
}

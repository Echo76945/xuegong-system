package com.xuegong.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScholarshipApplyMapper {

    /**
     * 按状态统计申请数量
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 按类型统计申请数量
     */
    List<Map<String, Object>> countByType();

    /**
     * 按类型统计申请金额
     */
    List<Map<String, Object>> sumAmountByType();

    /**
     * 按月统计申请数量
     */
    List<Map<String, Object>> countByMonth();

    /**
     * 统计总申请金额
     */
    Map<String, Object> sumTotalAmount();
}

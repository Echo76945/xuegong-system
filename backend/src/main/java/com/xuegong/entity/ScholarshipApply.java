package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "scholarship_apply")
public class ScholarshipApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "申请人不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @NotBlank(message = "类型不能为空")
    @Column(length = 20)
    private String type;

    @NotBlank(message = "名称不能为空")
    @Column(length = 100)
    private String name;

    @NotNull(message = "金额不能为空")
    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 20)
    private String status;

    @Column(name = "approval_stage", length = 20)
    private String approvalStage;

    @Column(name = "approver_remark", columnDefinition = "TEXT")
    private String approverRemark;

    @Column(name = "apply_time")
    private LocalDate applyTime;
}

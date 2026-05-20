package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "申请人不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @Column(name = "class_name", length = 50)
    private String className;

    @NotBlank(message = "外出地点不能为空")
    @Column(length = 100)
    private String destination;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotBlank(message = "外出事由不能为空")
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 20)
    private String status;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @PrePersist
    public void prePersist() {
        this.applyTime = LocalDateTime.now();
        if (this.status == null) this.status = "待审核";
    }
}

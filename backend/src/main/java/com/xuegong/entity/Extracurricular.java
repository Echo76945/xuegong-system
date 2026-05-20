package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "extracurricular")
public class Extracurricular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学生姓名不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @NotBlank(message = "活动名称不能为空")
    @Column(name = "activity_name", length = 200)
    private String activityName;

    @Column(length = 20)
    private String category;

    private Integer points;

    private Double duration;

    private LocalDate date;

    @Column(length = 20)
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) this.status = "待认定";
    }
}

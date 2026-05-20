package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "late_record")
public class LateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(name = "student_code", length = 20)
    private String studentCode;

    @NotBlank(message = "学生姓名不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @Column(name = "class_name", length = 50)
    private String className;

    private LocalDate date;

    @Column(name = "return_time", length = 10)
    private String returnTime;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private Boolean abnormal;

    @PrePersist
    public void prePersist() {
        if (this.abnormal == null) this.abnormal = false;
    }
}

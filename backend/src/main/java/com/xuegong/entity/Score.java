package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(name = "student_code", length = 20)
    private String studentCode;

    @Column(name = "student_name", length = 50)
    private String studentName;

    @NotBlank(message = "课程名不能为空")
    @Column(name = "course_name", length = 100)
    private String courseName;

    @NotNull(message = "成绩不能为空")
    private Double score;

    @Column(length = 20)
    private String semester;
}

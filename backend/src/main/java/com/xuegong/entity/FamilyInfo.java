package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "family_info")
public class FamilyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(name = "student_code", length = 20)
    private String studentCode;

    @NotBlank(message = "学生姓名不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @NotBlank(message = "家长姓名不能为空")
    @Column(name = "parent_name", length = 50)
    private String parentName;

    @Column(length = 10)
    private String relation;

    @Column(name = "parent_phone", length = 20)
    private String parentPhone;

    @Column(length = 200)
    private String address;

    private Double income;

    @Column(length = 20)
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String remark;
}

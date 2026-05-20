package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "课程号不能为空")
    @Column(unique = true, length = 20)
    private String code;

    @NotBlank(message = "课程名不能为空")
    @Column(length = 100)
    private String name;

    @Column(length = 10)
    private String type;

    @NotNull(message = "学分不能为空")
    private Double credit;

    @Column(length = 50)
    private String teacher;

    @Column(length = 50)
    private String schedule;

    private Integer capacity;

    private Integer enrolled;
}

package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Column(unique = true, length = 20)
    private String code;

    @NotBlank(message = "姓名不能为空")
    @Column(length = 50)
    private String name;

    @Column(length = 4)
    private String gender;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(length = 20)
    private String phone;

    @Column(length = 10)
    private String status;
}

package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "award")
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学生姓名不能为空")
    @Column(name = "student_name", length = 50)
    private String studentName;

    @NotBlank(message = "奖项名称不能为空")
    @Column(name = "award_name", length = 200)
    private String awardName;

    @Column(length = 20)
    private String type;

    @Column(length = 20)
    private String level;

    @Column(name = "grant_unit", length = 100)
    private String grantUnit;

    @Column(name = "award_date")
    private LocalDate awardDate;

    private Double bonus;
}

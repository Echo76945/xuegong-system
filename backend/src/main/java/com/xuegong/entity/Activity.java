package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "活动名称不能为空")
    @Column(length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate date;

    @Column(length = 100)
    private String location;

    private Integer capacity;

    private Integer enrolled;

    @Column(length = 20)
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.enrolled == null) this.enrolled = 0;
        if (this.status == null) this.status = "报名中";
    }
}

package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "incident")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Column(length = 200)
    private String title;

    @NotBlank(message = "上报人不能为空")
    @Column(length = 50)
    private String reporter;

    @Column(length = 20)
    private String category;

    @Column(length = 20)
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "report_time")
    private LocalDateTime reportTime;

    @Column(length = 20)
    private String status;

    @Column(length = 50)
    private String handler;

    @PrePersist
    public void prePersist() {
        this.reportTime = LocalDateTime.now();
        if (this.status == null) this.status = "待处理";
    }
}

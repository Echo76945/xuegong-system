package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcement")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Column(length = 200)
    private String title;

    @NotBlank(message = "内容不能为空")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String scope;

    @Column(length = 50)
    private String author;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    private Integer views;

    private Boolean pinned;

    @PrePersist
    public void prePersist() {
        this.publishTime = LocalDateTime.now();
        if (this.views == null) this.views = 0;
        if (this.pinned == null) this.pinned = false;
    }
}

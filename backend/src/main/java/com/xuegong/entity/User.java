package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Column(unique = true, length = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Column(length = 100)
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Column(length = 50)
    private String name;

    @Column(length = 20)
    private String role;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String status;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = "启用";
        }
        if (this.role == null) {
            this.role = "学生";
        }
    }
}

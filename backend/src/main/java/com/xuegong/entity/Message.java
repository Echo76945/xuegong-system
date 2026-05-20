package com.xuegong.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "sender_name", length = 50)
    private String senderName;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "is_read")
    private Boolean isRead;

    @PrePersist
    public void prePersist() {
        this.sendTime = LocalDateTime.now();
        if (this.isRead == null) this.isRead = false;
    }
}

package com.xuegong.repository;

import com.xuegong.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderIdOrReceiverId(Long senderId, Long receiverId);

    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
}

package com.xuegong.service;

import com.xuegong.entity.Message;
import com.xuegong.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;

    public List<Message> findAll() {
        return repository.findAll();
    }

    public Message findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Message> findByUserId(Long userId) {
        return repository.findBySenderIdOrReceiverId(userId, userId);
    }

    public List<Message> findConversation(Long userId1, Long userId2) {
        return repository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(userId1, userId2, userId2, userId1);
    }

    public Message save(Message message) {
        return repository.save(message);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public Message markAsRead(Long id) {
        Message existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setIsRead(true);
            return repository.save(existing);
        }
        return null;
    }
}

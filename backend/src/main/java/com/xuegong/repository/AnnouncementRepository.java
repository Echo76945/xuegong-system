package com.xuegong.repository;

import com.xuegong.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByTitleContaining(String title);

    List<Announcement> findByScope(String scope);

    List<Announcement> findByPinnedTrue();
}

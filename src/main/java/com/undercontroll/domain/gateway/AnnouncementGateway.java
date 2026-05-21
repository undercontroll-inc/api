package com.undercontroll.domain.gateway;

import com.undercontroll.domain.enums.AnnouncementType;
import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.model.PaginatedResult;

import java.util.List;
import java.util.Optional;

public interface AnnouncementGateway {

    Announcement save(Announcement announcement);

    void deleteById(Integer id);

    Optional<Announcement> findById(Integer id);

    List<Announcement> findAll();

    PaginatedResult<Announcement> findAllPaginated(int page, int size, AnnouncementType type);

    Optional<Announcement> findLastAnnouncement();

}

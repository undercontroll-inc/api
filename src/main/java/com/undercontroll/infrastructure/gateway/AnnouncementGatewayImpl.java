package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.model.Announcement;
import com.undercontroll.domain.gateway.AnnouncementGateway;
import com.undercontroll.infrastructure.mapper.AnnouncementMapper;
import com.undercontroll.infrastructure.persistence.entity.AnnouncementJpaEntity;
import com.undercontroll.infrastructure.persistence.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnnouncementGatewayImpl implements AnnouncementGateway {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;

    @Override
    public Announcement save(Announcement announcement) {
        AnnouncementJpaEntity jpaEntity = announcementMapper.toEntityWithId(announcement);
        AnnouncementJpaEntity savedEntity = announcementRepository.save(jpaEntity);
        return announcementMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Integer id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public Optional<Announcement> findById(Integer id) {
        return announcementRepository.findById(id).map(announcementMapper::toDomain);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementRepository.findAll().stream()
                .map(announcementMapper::toDomain)
                .toList();
    }

    @Override
    public List<Announcement> findAllPaginated(int page, int size) {
        return announcementRepository.findAllPaginated(PageRequest.of(page, size)).stream()
                .map(announcementMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Announcement> findLastAnnouncement() {
        return announcementRepository.findLastAnnouncement()
                .map(announcementMapper::toDomain);
    }

}

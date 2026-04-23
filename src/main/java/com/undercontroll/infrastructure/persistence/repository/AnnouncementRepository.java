package com.undercontroll.infrastructure.persistence.repository;

import com.undercontroll.domain.enums.AnnouncementType;
import com.undercontroll.infrastructure.persistence.entity.AnnouncementJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementJpaEntity, Integer> {

    @Query("SELECT a FROM AnnouncementJpaEntity a WHERE (:type IS NULL OR a.type = :type) ORDER BY a.publishedAt DESC")
    Page<AnnouncementJpaEntity> findAllPaginated(Pageable pageable, @Param("type") AnnouncementType type);

    @Query("SELECT a FROM AnnouncementJpaEntity a ORDER BY a.publishedAt DESC LIMIT 1")
    Optional<AnnouncementJpaEntity> findLastAnnouncement();
}

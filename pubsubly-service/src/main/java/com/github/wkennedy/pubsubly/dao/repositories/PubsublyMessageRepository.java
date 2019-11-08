package com.github.wkennedy.pubsubly.dao.repositories;

import com.github.wkennedy.pubsubly.dao.entities.PubsublyMessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PubsublyMessageRepository extends JpaRepository<PubsublyMessagesEntity, String> {
}

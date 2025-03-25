package com.study.url_shorter.domain.url.repository;

import com.study.url_shorter.domain.url.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortUrlAndIsDeletedFalse(String shortUrl);
    List<Url> findAllByIsDeletedFalse();
}
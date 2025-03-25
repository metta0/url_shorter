package com.study.url_shorter.domain.url.service;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;
import com.study.url_shorter.domain.url.entity.Url;
import com.study.url_shorter.domain.url.repository.UrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UrlServiceImpl {

    private final UrlRepository urlRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto requestDto) {
        String shortUrl = generateShortUrl();
        Url url = new Url(shortUrl, requestDto.getOriginalUrl());
        urlRepository.save(url);
        return new UrlResponseDto(shortUrl, requestDto.getOriginalUrl());
    }

    private String generateShortUrl() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        String shortUrl = sb.toString();
        // 중복 체크 (단순화된 예시, 실제로는 재귀 호출 등으로 개선 가능)
        if (urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl).isPresent()) {
            return generateShortUrl();
        }
        return shortUrl;
    }

    public Optional<List<UrlResponseDto>> getAllUrls(){
        List<Url> urls = urlRepository.findAllByIsDeletedFalse();

        if (urls.isEmpty()) {
            return Optional.empty();
        }

        List<UrlResponseDto> urlResponseDtos = urls.stream()
                .map(url -> new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl()))
                .collect(Collectors.toList());

        return Optional.of(urlResponseDtos);
    }

    public Optional<UrlResponseDto> getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .map(url -> new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl()));
    }

    @Transactional
    public UrlResponseDto updateShortUrl(String shortUrl, UrlRequestDto requestDto) {
        Url url = urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .orElseThrow(() -> new NoSuchElementException());
        url.setOriginalUrl(requestDto.getOriginalUrl()); // 핵심 상태 재정의
        urlRepository.save(url); // updated_at은 서버가 관리
        return new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl());
    }

    @Transactional
    public void deleteShortUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .orElseThrow(() -> new NoSuchElementException());
        url.setIsDeleted(true);
        urlRepository.save(url);
    }
}
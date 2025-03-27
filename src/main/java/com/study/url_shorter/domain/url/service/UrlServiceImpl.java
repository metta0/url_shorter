package com.study.url_shorter.domain.url.service;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;
import com.study.url_shorter.domain.url.entity.Url;
import com.study.url_shorter.domain.url.repository.UrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UrlServiceImpl {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, UrlResponseDto> redisTemplate; // RedisTemplate 추가
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6;
    private static final String CACHE_PREFIX = "url:"; // 캐시 키 prefix
    private static final Duration TTL = Duration.ofHours(24); // TTL 24시간

    public UrlServiceImpl(UrlRepository urlRepository, RedisTemplate<String, UrlResponseDto> redisTemplate) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
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
        // 1. Redis에서 먼저 확인
        String cacheKey = CACHE_PREFIX + shortUrl;
        UrlResponseDto cached = redisTemplate.opsForValue().get(cacheKey);
        if(cached != null){
            return Optional.of(cached); // 캐시 히트
        }

        // 2. 캐시 미스 시 DB 조회
        Optional<UrlResponseDto> result = urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .map(url -> new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl()));


        // 3. DB에서 가져온 데이터를 Redis에 저장
        result.ifPresent(dto -> redisTemplate.opsForValue().set(cacheKey, dto, TTL));
        return result;
    }

    @Transactional
    public UrlResponseDto updateShortUrl(String shortUrl, UrlRequestDto requestDto) {
        Url url = urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .orElseThrow(() -> new NoSuchElementException());
        url.setOriginalUrl(requestDto.getOriginalUrl()); // 핵심 상태 재정의
        urlRepository.save(url); // updated_at은 서버가 관리
        UrlResponseDto responseDto = new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl());

        // 캐시 업데이트
        String cacheKey = CACHE_PREFIX + shortUrl;
        redisTemplate.opsForValue().set(cacheKey, responseDto, TTL);
        return responseDto;
    }

    @Transactional
    public void deleteShortUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrlAndIsDeletedFalse(shortUrl)
                .orElseThrow(() -> new NoSuchElementException());
        url.setIsDeleted(true);
        urlRepository.save(url);

        // 캐시 삭제
        String cacheKey = CACHE_PREFIX + shortUrl;
        redisTemplate.delete(cacheKey);
    }
}
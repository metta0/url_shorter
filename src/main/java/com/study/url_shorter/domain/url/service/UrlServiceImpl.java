package com.study.url_shorter.domain.url.service;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;
import com.study.url_shorter.domain.url.entity.Url;
import com.study.url_shorter.domain.url.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UrlServiceImpl {

    private final UrlRepository urlRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponseDto createShortUrl(UrlRequestDto requestDto) {
        String shortUrl = generateShortUrl();
        Url url = new Url(shortUrl, requestDto.getOriginalUrl());
        urlRepository.save(url);
        return new UrlResponseDto(shortUrl, requestDto.getOriginalUrl());
    }

    public Optional<UrlResponseDto> getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .map(url -> new UrlResponseDto(url.getShortUrl(), url.getOriginalUrl()));
    }

    private String generateShortUrl() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        String shortUrl = sb.toString();
        // 중복 체크 (단순화된 예시, 실제로는 재귀 호출 등으로 개선 가능)
        if (urlRepository.findByShortUrl(shortUrl).isPresent()) {
            return generateShortUrl();
        }
        return shortUrl;
    }
}
package com.study.url_shorter.domain.url.service;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;

import java.util.Optional;

public interface UrlService {

    UrlResponseDto createShortUrl(UrlRequestDto requestDto);
    Optional<UrlRequestDto> getOriginalUrl(String shortUrl);
}

package com.study.url_shorter.domain.url.service;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;

import java.util.List;
import java.util.Optional;

public interface UrlService {

    UrlResponseDto createShortUrl(UrlRequestDto requestDto);

    Optional<List<UrlResponseDto>> getAllUrls();
    Optional<UrlRequestDto> getOriginalUrl(String shortUrl);

    void deleteShortUrl(String shortUrl);

}

package com.study.url_shorter.domain.url.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlResponseDto {

    private String shortUrl;
    private String originalUrl;

    public UrlResponseDto(String shortUrl, String originalUrl) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }
}
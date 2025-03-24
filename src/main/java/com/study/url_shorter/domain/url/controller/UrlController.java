package com.study.url_shorter.domain.url.controller;

import com.study.url_shorter.domain.url.dto.UrlRequestDto;
import com.study.url_shorter.domain.url.dto.UrlResponseDto;
import com.study.url_shorter.domain.url.service.UrlServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlServiceImpl urlService;

    public UrlController(UrlServiceImpl urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto requestDto) {
        UrlResponseDto responseDto = urlService.createShortUrl(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<UrlResponseDto> getOriginalUrl(@PathVariable("shortUrl") String shortUrl) {
        return urlService.getOriginalUrl(shortUrl)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
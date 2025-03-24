package com.study.url_shorter.domain.url.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;
}
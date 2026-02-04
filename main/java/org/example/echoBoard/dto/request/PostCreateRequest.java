package org.example.echoBoard.dto.request;

import jakarta.persistence.Lob;
import lombok.Getter;


@Getter
public class PostCreateRequest {

    private String title;

    @Lob
    private String content;
}

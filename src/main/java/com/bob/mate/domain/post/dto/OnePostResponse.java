package com.bob.mate.domain.post.dto;

import com.bob.mate.domain.post.entity.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OnePostResponse {
    private String title;
    private String content;
    private String profileUrl;
    private String username;
    private OffsetDateTime createdAt;
    private Integer likeCount;
    private Integer viewCount;

    @QueryProjection
    public OnePostResponse(
            String title, String content, String profileUrl,
            String username, OffsetDateTime createdAt,
            Integer likeCount, Integer viewCount
    ) {
        this.title = title;
        this.content = content;
        this.profileUrl = profileUrl;
        this.username = username;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
    }
}
package com.bob.mate.domain.post.repository;

import com.bob.mate.domain.post.dto.AllPostResponse;
import com.bob.mate.domain.post.dto.OnePostResponse;
import com.bob.mate.domain.post.dto.QAllPostResponse;
import com.bob.mate.domain.post.dto.QOnePostResponse;
import com.bob.mate.domain.post.entity.PostLike;
import com.bob.mate.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.bob.mate.domain.post.entity.QPost.post;
import static com.bob.mate.domain.post.entity.QPostLike.postLike;
import static com.bob.mate.domain.user.entity.QUploadFile.uploadFile;
import static com.bob.mate.domain.user.entity.QUser.user;
import static com.bob.mate.domain.user.entity.QUserProfile.userProfile;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AllPostResponse> findAllPosts(Pageable pageable) {
        List<AllPostResponse> posts = jpaQueryFactory
                .select(new QAllPostResponse(
                        post.id, post.title, uploadFile.storeFilename , userProfile.nickName,
                        post.timeEntity.createdDate, post.comments.size(), post.likeCount,
                        post.viewCount, userProfile.address
                ))
                .from(post)
                .innerJoin(post.user, user)
                .innerJoin(user.userProfile, userProfile)
                .innerJoin(userProfile.uploadFile, uploadFile)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.timeEntity.createdDate.desc())
                .fetch();

        return PageableExecutionUtils.getPage(posts, pageable, () -> (long) posts.size());
    }

    @Override
    public Optional<OnePostResponse> findPost(Long postId, User member) {
        jpaQueryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        Optional<PostLike> optionalPostLike = Optional.ofNullable(jpaQueryFactory
                .selectFrom(postLike)
                .innerJoin(postLike.post, post).fetchJoin()
                .innerJoin(postLike.user, user).fetchJoin()
                .where(post.id.eq(postId).and(user.id.eq(member.getId())))
                .fetchOne());

        Optional<OnePostResponse> res = Optional.ofNullable(jpaQueryFactory
                .select(new QOnePostResponse(
                        post.id, post.title, post.content, uploadFile.storeFilename,
                        userProfile.nickName, post.timeEntity.createdDate,
                        post.likeCount, post.viewCount, userProfile.address
                ))
                .from(post)
                .innerJoin(post.user, user)
                .innerJoin(user.userProfile, userProfile)
                .innerJoin(userProfile.uploadFile, uploadFile)
                .where(post.id.eq(postId))
                .fetchOne());

        if (optionalPostLike.isPresent() && res.isPresent()) res.get().setLiked(Boolean.TRUE);
        else if (optionalPostLike.isEmpty() && res.isPresent()) res.get().setLiked(Boolean.FALSE);

        return res;
    }
}

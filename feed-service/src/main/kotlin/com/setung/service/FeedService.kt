package com.setung.service

import com.setung.repo.FeedRepository
import org.springframework.stereotype.Service

@Service
class FeedService(
    private val feedRepository: FeedRepository
) {

    fun getFeed(userId: Long, lastPostId: Double, limit: Long) = feedRepository.getFeed(userId, lastPostId, limit)


}


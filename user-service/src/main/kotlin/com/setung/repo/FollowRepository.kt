package com.setung.repo

import com.setung.entity.FollowEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<FollowEntity, Long>
package com.setung.userservice.repo

import com.setung.userservice.entity.FollowEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<FollowEntity, Long>
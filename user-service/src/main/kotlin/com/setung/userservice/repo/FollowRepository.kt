package com.setung.userservice.repo

import com.setung.userservice.entity.Follow
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow, Long>
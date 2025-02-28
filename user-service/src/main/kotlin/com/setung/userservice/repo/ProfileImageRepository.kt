package com.setung.userservice.repo

import com.setung.userservice.entity.ProfileImage
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileImageRepository : JpaRepository<ProfileImage, Long>
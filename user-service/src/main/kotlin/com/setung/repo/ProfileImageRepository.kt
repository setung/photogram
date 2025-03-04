package com.setung.repo

import com.setung.entity.ProfileImage
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileImageRepository : JpaRepository<ProfileImage, Long>
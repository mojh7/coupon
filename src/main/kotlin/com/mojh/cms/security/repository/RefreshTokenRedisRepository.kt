package com.mojh.cms.security.repository

import com.mojh.cms.security.entity.RefreshTokenRedis
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRedisRepository : CrudRepository<RefreshTokenRedis, String>
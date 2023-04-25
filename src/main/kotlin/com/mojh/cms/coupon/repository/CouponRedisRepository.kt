package com.mojh.cms.coupon.repository

import com.mojh.cms.coupon.entity.CouponRedis
import org.springframework.data.repository.CrudRepository

interface CouponRedisRepository : CrudRepository<CouponRedis, Long>
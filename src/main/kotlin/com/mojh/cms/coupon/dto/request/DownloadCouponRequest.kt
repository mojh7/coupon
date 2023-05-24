package com.mojh.cms.coupon.dto.request

import java.time.Instant

data class DownloadCouponRequest(
    val requestDateTime: Instant
)
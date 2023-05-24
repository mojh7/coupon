package com.mojh.cms.common.embeddable

import java.time.Instant
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Period(
    @Column
    var startAt: Instant,

    @Column
    var endAt: Instant
) {
    init {
        require(startAt <= endAt) { "시작 일시는 종료 일시보다 빠르거나 같아야 합니다." }
    }
    /**
     * 시작 일시 <= dateTime <= 종료 일시
     */
    fun contains(dateTime: Instant) = (startAt..endAt).contains(dateTime)
}




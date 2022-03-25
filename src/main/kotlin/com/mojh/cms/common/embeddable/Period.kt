package com.mojh.cms.common.embeddable

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Period(
    @Column var startAt: LocalDateTime,
    @Column var endAt: LocalDateTime
) {
    /**
     * 시작기간 <= now <= 종료기간
     */
    fun isValid(now: LocalDateTime) = !(now.isBefore(startAt) || now.isAfter(endAt))
}
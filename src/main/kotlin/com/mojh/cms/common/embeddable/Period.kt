package com.mojh.cms.common.embeddable

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Period(
    @Column
    var startAt: LocalDateTime,

    @Column
    var endAt: LocalDateTime
) {
    init {
        require(startAt <= endAt) { "시작 일시는 종료 일시보다 같거나 빨라야 합니다." }
    }
    /**
     * 시작 일시 <= dateTime <= 종료 일시
     */
    fun contains(dateTime: LocalDateTime) = (startAt..endAt).contains(dateTime)
}




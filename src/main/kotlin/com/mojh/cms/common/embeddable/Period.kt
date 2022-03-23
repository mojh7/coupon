package com.mojh.cms.common.embeddable

import java.time.LocalDateTime
import javax.persistence.Embeddable

@Embeddable
class Period(
    var startAt: LocalDateTime,
    var endAt: LocalDateTime
)
package com.mojh.cms.event.repository

import com.mojh.cms.event.entity.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Long> {
}
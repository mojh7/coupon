package com.mojh.cms.common

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val seq: Long? = null

    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(updatable = false)
    lateinit var updatedAt: LocalDateTime

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false

        if (seq != other.seq) return false

        return true
    }

    override fun hashCode(): Int {
        return seq?.hashCode() ?: 0
    }
}
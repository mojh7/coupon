package com.mojh.cms.security.entity

import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.util.concurrent.TimeUnit
import javax.persistence.Id


@RedisHash("refreshTokenChain")
class RefreshTokenRedis(
    accountId: String,
    tokenChainId: String,
    tokenId: String,
    ttl: Long
) {
    companion object {
        /**
         * 여러 개의 변수를 조합한 id 생성 방식이다 보니 id에 조합될 변수가 바뀌거나 구분 문자가 변경될 수 있음
         * 해당 entity 외에도 repository layer를 사용하여 entity를 조회하는 등의 작업을 할 때
         * 직접 id를 조합하는 방식보다는 함수를 통해 id를 생성하는 것이 나아보여 함수 제공
         */
        fun generateId(accountId: String, tokenChainId: String) = "$accountId:$tokenChainId"
    }

    @Id
    var id: String = generateId(accountId, tokenChainId)
        protected set

    val accountId: String = accountId

    val tokenChainId: String = tokenChainId

    var tokenId: String = tokenId
        protected set

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    var ttl: Long = ttl
        protected set
}
package com.mojh.cms.security.jwt

import com.mojh.cms.common.BaseTest
import com.mojh.cms.common.config.RedissonConfig
import com.mojh.cms.common.exception.CustomException
import com.mojh.cms.common.exception.ErrorCode
import com.mojh.cms.security.BEARER_PREFIX
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldStartWith
import org.redisson.api.RSetCache
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.StringUtils

@BaseTest
@SpringBootTest(classes = [JwtTokenUtils::class, RedissonConfig::class])
internal class JwtTokenUtilsTest(
    private val jwtTokenUtils: JwtTokenUtils,
    private val redisson: RedissonClient
) : FunSpec() {

    companion object {
        private const val ACCOUNT_ID = "testAccountId";
        private const val JWT_HEADER_BASE64 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.";
        private const val EXPIRED_ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhY2Nlc3MiLCJpZCI6InRlc3RBY2NvdW50SWQiLCJpYXQiOjE2NDc1MDE1NDYsImV4cCI6MTY0NzUwMTYzNn0.US3LiOA6B4Wtq9PJnxZPBrLJ_SREYVDKkf-wblshSRSI4dlZnnxvB4FyKFLCVRC97LEhivEmPHC4OkkgiHuECg";
        private const val EXPIRED_REFRESH_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2NDc1MDI4NzYsImV4cCI6MTY0NzUwMzAyNn0.1RurnEP4bNLxlEZ8CBxvzsSguqxB2lOhfBoIAcMnQGMRs4l-HVwePjbKHWaEKMKGa3WxYE7IMknQuzrrykPB2Q"
        private const val INVALID_TOKEN = "invalidToken"
    }

    init {
        isolationMode = IsolationMode.InstancePerLeaf



        test("액세스 토큰 생성 시 유효한 토큰이 반환된다") {
            val actualAccessToken = jwtTokenUtils.createAccessToken(ACCOUNT_ID)

            assertSoftly(actualAccessToken) {
                StringUtils.containsWhitespace(this) shouldBe false
                this shouldStartWith JWT_HEADER_BASE64
            }

        }

        test("리프레쉬 토큰 생성 성공시 유효한 토큰이 반환된다") {
            val actualRefreshToken = jwtTokenUtils.createRefreshToken()

            assertSoftly(actualRefreshToken) {
                StringUtils.containsWhitespace(this) shouldBe false
                this shouldStartWith JWT_HEADER_BASE64
            }
        }


        context("토큰에서 계정 아이디를 파싱한다") {
            context("액세스 토큰이") {
                test("유효할 때 파싱 시 계정 아이디가 반환된다") {
                    val accessToken = jwtTokenUtils.createAccessToken(ACCOUNT_ID)

                    val actualAccountId = jwtTokenUtils.parseAccountId(accessToken)

                    actualAccountId shouldBe ACCOUNT_ID
                }

                test("만료됐더라도 파싱 시 계정 아이디가 반환된다") {
                    val actualAccountId = jwtTokenUtils.parseAccountId(EXPIRED_ACCESS_TOKEN)

                    actualAccountId shouldBe ACCOUNT_ID
                }
            }

            context("리프레쉬 토큰은") {
                test("유효하더라도 파싱 시 CustomException 'INVALID_TOKEN'을 던진다") {
                    val refreshToken = jwtTokenUtils.createRefreshToken()

                    shouldThrow<CustomException> {
                        jwtTokenUtils.parseAccountId(refreshToken)
                    }.errorCode shouldBe ErrorCode.INVALID_TOKEN
                }

                // Jwts에서 requireSubject("access") subject : "access" 인지 확인하기 때문
                test("만료됐어도 파싱 시 CustomException 'INVALID_TOKEN'을 던진다") {
                    shouldThrow<CustomException> {
                        jwtTokenUtils.parseAccountId(EXPIRED_REFRESH_TOKEN)
                    }.errorCode shouldBe ErrorCode.INVALID_TOKEN
                }
            }

            test("유효하지 않은 토큰으로 파싱 시 CustomException 'INVALID_TOKEN'을 던진다") {
                shouldThrow<CustomException> {
                    jwtTokenUtils.parseAccountId(INVALID_TOKEN)
                }.errorCode shouldBe ErrorCode.INVALID_TOKEN
            }
        }

        context("토큰의 남은 만료 시간을 반환한다") {
            context("액세스 토큰이") {
                test("유효할 때 남은 만료 시간이 반환된다") {
                    val token = jwtTokenUtils.createAccessToken(ACCOUNT_ID);

                    val actual = jwtTokenUtils.getRemainingExpirationTime(token)

                    actual shouldBeGreaterThan 0
                }

                test("만료됐으면 남은 만료 시간을 확인할 경우 CustomException 'EXPIRED_TOKEN'을 던진다") {
                    shouldThrow<CustomException> {
                        jwtTokenUtils.getRemainingExpirationTime(EXPIRED_ACCESS_TOKEN)
                    }.errorCode shouldBe ErrorCode.EXPIRED_TOKEN
                }
            }

            context("리프레쉬 토큰이") {
                test("유효할 때 남은 만료 시간이 반환된다") {
                    val token = jwtTokenUtils.createRefreshToken();

                    val actual = jwtTokenUtils.getRemainingExpirationTime(token)

                    actual shouldBeGreaterThan 0
                }

                test("만료됐으면 남은 만료 시간을 확인할 경우 CustomException 'EXPIRED_TOKEN'을 던진다") {
                    shouldThrow<CustomException> {
                        jwtTokenUtils.getRemainingExpirationTime(EXPIRED_REFRESH_TOKEN)
                    }.errorCode shouldBe ErrorCode.EXPIRED_TOKEN
                }
            }


            test("유효하지 않은 토큰으로 남은 만료 시간을 확인할 경우 CustomException 'INVALID_TOKEN'을 던진다") {
                shouldThrow<CustomException> {
                    jwtTokenUtils.getRemainingExpirationTime(INVALID_TOKEN)
                }.errorCode shouldBe ErrorCode.INVALID_TOKEN
            }
        }

        context("토큰이 유효한지 확인한다") {
            context("액세스 토큰이") {
                test("유효할 때 true가 반환된다") {
                    val token = jwtTokenUtils.createAccessToken(ACCOUNT_ID);

                    val result = jwtTokenUtils.validateToken(token)

                    result shouldBe true
                }

                test("만료됐으면 CustomException 'EXPIRED_TOKEN'을 던진다") {
                    shouldThrow<CustomException> {
                        jwtTokenUtils.validateToken(EXPIRED_ACCESS_TOKEN)
                    }.errorCode shouldBe ErrorCode.EXPIRED_TOKEN
                }
            }

            context("리프레쉬 토큰이") {
                test("유효할 때 true가 반환된다") {
                    val token = jwtTokenUtils.createRefreshToken();

                    val result = jwtTokenUtils.validateToken(token)

                    result shouldBe true
                }

                test("만료됐으면 CustomException 'EXPIRED_TOKEN'을 던진다") {
                    shouldThrow<CustomException> {
                        jwtTokenUtils.validateToken(EXPIRED_REFRESH_TOKEN)
                    }.errorCode shouldBe ErrorCode.EXPIRED_TOKEN
                }
            }


            test("유효하지 않은 토큰으로 유효성 검사를 하면 CustomException 'INVALID_TOKEN'을 던진다") {
                shouldThrow<CustomException> {
                    jwtTokenUtils.validateToken(INVALID_TOKEN)
                }.errorCode shouldBe ErrorCode.INVALID_TOKEN
            }
        }

        // TODO: test
        /*context("차단된 액세스 토큰인지 확인한다") {
            //given
            val registeredAccessToken = jwtTokenUtils.createAccessToken(ACCOUNT_ID)
            // access token blacklist 등록
            redisson.getSetCache<String>(ACCESS_TOKEN_REDIS_KEY_PREFIX + ACCOUNT_ID)
                .add(registeredAccessToken, jwtTokenUtils.getRemainingExpirationTime(registeredAccessToken), TimeUnit.MILLISECONDS)

            test("등록되어 있는 액세스 토큰으로 차단 여부 확인시 true가 반환된다") {
                val result = jwtTokenUtils.isBlockedAccessToken(registeredAccessToken, ACCOUNT_ID)

                result shouldBe true
            }

            test("등록되지 않은 액세스 토큰으로 차단 여부 확인시 false가 반환된다") {
                val otherToken = EXPIRED_ACCESS_TOKEN

                val result = jwtTokenUtils.isBlockedAccessToken(otherToken, ACCOUNT_ID)

                assertSoftly {
                    registeredAccessToken shouldNotBe otherToken
                    result shouldBe false
                }
            }
        }*/

        test("레디스에 계정 아이디에 해당하는 액세스 토큰 Set을 조회한다") {
            val result = jwtTokenUtils.getAccessTokenRSetCache(ACCOUNT_ID)

            assertSoftly(result) {
                (this is RSetCache<String>) shouldBe true
                size shouldBe 0
                isEmpty() shouldBe true
                isExists shouldBe false
            }
        }

        test("레디스에 계정 아이디에 해당하는 리프레쉬 토큰 Set을 조회한다") {
            val result = jwtTokenUtils.getRefreshTokenRSetCache(ACCOUNT_ID)

            assertSoftly(result) {
                (this is RSetCache<String>) shouldBe true
                isExists shouldBe false
                isEmpty() shouldBe true
                size shouldBe 0
            }
        }

        context("HTTP Authorization header에서 type을 제거하고 토큰 정보를 추출한다") {
            context("Authorization header가") {
                test("null이 아니고 올바른 type을 가질 때 토큰을 추출하면 토큰 정보가 반환된다") {
                    val header = BEARER_PREFIX + EXPIRED_ACCESS_TOKEN

                    val token = jwtTokenUtils.extractTokenFrom(header)

                    assertSoftly(token) {
                        this shouldStartWith JWT_HEADER_BASE64
                        this shouldBe EXPIRED_ACCESS_TOKEN
                    }
                }

                test("null이 아니지만 올바른 type이 아닐 때 토큰을 추출하면 null이 반환된다") {
                    val header = "invalid"

                    val result = jwtTokenUtils.extractTokenFrom(header)

                    assertSoftly {
                        header shouldHaveMinLength 1
                        result shouldBe null
                    }
                }

                test("null이 아니지만 값이 비어있을 때 토큰을 추출하면 null이 반환된다") {
                    val header = ""

                    val result = jwtTokenUtils.extractTokenFrom(header)

                    assertSoftly {
                        header shouldHaveLength 0
                        result shouldBe null
                    }
                }

                test("null이면 토큰을 추출할 때 null이 반환된다") {
                    val header: String? = null

                    val result = jwtTokenUtils.extractTokenFrom(header)

                    assertSoftly {
                        header shouldBe null
                        result shouldBe null
                    }
                }
            }
        }
    }
}
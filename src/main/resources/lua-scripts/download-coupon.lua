local coupon_id = KEYS[1]
local coupon_key = 'coupon:'..coupon_id
local coupon_downloaders_key = KEYS[2]..':'..coupon_id
local coupon_issuance_queue_key = KEYS[3]
local customer_id = ARGV[1]
local enabled = ARGV[2]
local now = tonumber(ARGV[3])
local coupon_issuance_queue_value = coupon_id..':'..customer_id
local COUPON_NOT_ENABLED = ARGV[4]
local COUPON_ISSUE_PERIOD_INVALID = ARGV[5]
local COUPON_EXHAUSTED = ARGV[6]
local ALREADY_DOWNLOADED_COUPON = ARGV[7]

-- 쿠폰 발급 가능 여부 확인
-- 활성화 상태인지 확인
local status = redis.call('HGET', coupon_key, 'status')
if status ~= enabled then
  return COUPON_NOT_ENABLED
end

-- 발급 가능 시간 비교
local start_at = tonumber(redis.call('HGET', coupon_key, 'startAt'))
local end_at = tonumber(redis.call('HGET', coupon_key, 'endAt'))
if (now < start_at or now > end_at) then
  return COUPON_ISSUE_PERIOD_INVALID
end

-- 개수 확인
local max_count = tonumber(redis.call('HGET', coupon_key, 'maxCount'))
local curr_count = tonumber(redis.call('SCARD', coupon_downloaders_key))
if (curr_count >= max_count) then
  return COUPON_EXHAUSTED
end

-- 중복 발급 여부 확인
if (redis.call('SISMEMBER', coupon_downloaders_key, customer_id) == 1) then
  return ALREADY_DOWNLOADED_COUPON
end

-- 쿠폰 발급 요청 성공한 유저 목록에 추가
redis.call('SADD', coupon_downloaders_key, customer_id)

-- 쿠폰 발급 요청 대기 큐에 쿠폰과 유저 id 정보 추가
redis.call('ZADD', coupon_issuance_queue_key, now, coupon_issuance_queue_value)

return 'SUCCESS'
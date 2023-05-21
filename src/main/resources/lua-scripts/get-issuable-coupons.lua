local issuable_coupons = redis.call('ZRANGE', KEYS[1], 0, ARGV[1] - 1)
redis.call('ZREMRANGEBYRANK', KEYS[1], 0, ARGV[1] - 1)
return issuable_coupons
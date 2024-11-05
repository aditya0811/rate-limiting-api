1) The solution is an extension of Token Bucket algorithm for rate limiting API. The algorithm allows certain 
number of request within duration of time. Each request takes certain tokens(in our case its 1).  And refills the bucket, 
with tokens, so subsequent request can use the newly added tokens after certain duration.
However, this does not include penalty if tokens are not present. To stop API from being called for a certain time 
period, we customized the algorithm with two additional attributes - `lastFailedTimestamp` and `penaltyInSeconds`.
We have implemented a `TokenBucket` class accepting three arguments. 
- `maxBucketSize` - total requests allowed (1 token used by 1 request) for particular duration.
- `penaltyInSeconds` - stop API from being called for this many seconds.
- `refillInSeconds` - repopulate tokens after this many seconds.

Non-constructor class attributes
- `currentBucketSize` - represents current size of bucket.
- `lastRefillTimestamp` - represents timestamp when last bucket was refilled, helps us to repopulate tokens.
- `lastFailedTimestamp` - represents timestamp when last request was failed, as tokens were not present, helps us to avoid request till
`lastFailedTimestamp` + `penaltyInSeconds`
2) The `penaltyInSeconds` is a additional parameter that we are using, as part of the solution.
We initialize `lastFailedTimestamp` using this, assuming penalty happened `penaltyInSeconds` ago. And whenever, we check if 
request is allowed, we look at difference between current time and `lastFailedTimestamp`. 
If `lastFailedTimestamp` was  `penaltyInSeconds` ago, then we will consider tokens and allow the request accordingly.
3) The project can be built using `mvn clean install` with Java 11, after `cd rate-limiting-api`. And the class can be run using
`java -jar target/rate-limiting-api-1.0-SNAPSHOT.jar`. Here the `TokenBucket` allows 15 request within a minute, and again after a minute populates 15 tokens to be used, 1
token for each request. We put `Thread.sleep(60000)` not as part of solution, but to allow enough time to execute request post penalty. We observed
request(0-index) 16th, 32nd, 48th skipped, resulting in 1 minute penalty. Following is the log, where timestamp is in seconds.
```
Request 0 : ----API RESPONSE----  is made at: 1730831347
Request 1 : ----API RESPONSE----  is made at: 1730831347
Request 2 : ----API RESPONSE----  is made at: 1730831347
Request 3 : ----API RESPONSE----  is made at: 1730831347
Request 4 : ----API RESPONSE----  is made at: 1730831347
Request 5 : ----API RESPONSE----  is made at: 1730831347
Request 6 : ----API RESPONSE----  is made at: 1730831347
Request 7 : ----API RESPONSE----  is made at: 1730831347
Request 8 : ----API RESPONSE----  is made at: 1730831347
Request 9 : ----API RESPONSE----  is made at: 1730831347
Request 10 : ----API RESPONSE----  is made at: 1730831347
Request 11 : ----API RESPONSE----  is made at: 1730831347
Request 12 : ----API RESPONSE----  is made at: 1730831347
Request 13 : ----API RESPONSE----  is made at: 1730831347
Request 14 : ----API RESPONSE----  is made at: 1730831347
Request 15 is NOT called at: 1730831347
Request 16 : ----API RESPONSE----  is made at: 1730831407
Request 17 : ----API RESPONSE----  is made at: 1730831407
Request 18 : ----API RESPONSE----  is made at: 1730831407
Request 19 : ----API RESPONSE----  is made at: 1730831407
Request 20 : ----API RESPONSE----  is made at: 1730831407
Request 21 : ----API RESPONSE----  is made at: 1730831407
Request 22 : ----API RESPONSE----  is made at: 1730831407
Request 23 : ----API RESPONSE----  is made at: 1730831407
Request 24 : ----API RESPONSE----  is made at: 1730831407
Request 25 : ----API RESPONSE----  is made at: 1730831407
Request 26 : ----API RESPONSE----  is made at: 1730831407
Request 27 : ----API RESPONSE----  is made at: 1730831407
Request 28 : ----API RESPONSE----  is made at: 1730831407
Request 29 : ----API RESPONSE----  is made at: 1730831407
Request 30 : ----API RESPONSE----  is made at: 1730831407
Request 31 is NOT called at: 1730831407
Request 32 : ----API RESPONSE----  is made at: 1730831467
Request 33 : ----API RESPONSE----  is made at: 1730831467
Request 34 : ----API RESPONSE----  is made at: 1730831467
Request 35 : ----API RESPONSE----  is made at: 1730831467
Request 36 : ----API RESPONSE----  is made at: 1730831467
Request 37 : ----API RESPONSE----  is made at: 1730831467
Request 38 : ----API RESPONSE----  is made at: 1730831467
Request 39 : ----API RESPONSE----  is made at: 1730831467
Request 40 : ----API RESPONSE----  is made at: 1730831467
Request 41 : ----API RESPONSE----  is made at: 1730831467
Request 42 : ----API RESPONSE----  is made at: 1730831467
Request 43 : ----API RESPONSE----  is made at: 1730831467
Request 44 : ----API RESPONSE----  is made at: 1730831467
Request 45 : ----API RESPONSE----  is made at: 1730831467
Request 46 : ----API RESPONSE----  is made at: 1730831467
Request 47 is NOT called at: 1730831467
Request 48 : ----API RESPONSE----  is made at: 1730831527
Request 49 : ----API RESPONSE----  is made at: 1730831527

```
4) If we want to handle 20 request per minute, assuming we cannot increase maximum bucket size. 
- We can consider caching, and if cache no hit, and no token is left, we will penalize, otherwise, 
will pick response from cache. 
- Another option to consider is run multiple instances at server side, proportionally, increasing the
API limit.
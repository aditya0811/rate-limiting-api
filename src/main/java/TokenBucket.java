public class TokenBucket {
  private final long maxBucketSize;
  private final long penaltyInSeconds;
  private final long refillInSeconds;
  private long currentBucketSize;
  private long lastRefillTimestamp;
  private long lastFailedTimestamp;

  /**
   *
   * @param maxBucketSize limit of APIs
   * @param penaltyInSeconds duration for which we cannot call API
   * @param refillInSeconds Time it takes bucket to refill
   */
  public TokenBucket(long maxBucketSize, long penaltyInSeconds, long refillInSeconds) {
    this.maxBucketSize = maxBucketSize;
    this.penaltyInSeconds = penaltyInSeconds;
    this.refillInSeconds = refillInSeconds;
    currentBucketSize = maxBucketSize;
    lastRefillTimestamp = getCurrentTimeInSeconds();
    lastFailedTimestamp = getCurrentTimeInSeconds() - penaltyInSeconds;
  }

  public synchronized boolean allowRequest(int tokens) {
    if (getCurrentTimeInSeconds() - lastFailedTimestamp < penaltyInSeconds) {
      return false;
    }
    refill();
    if(currentBucketSize >= tokens) {
      currentBucketSize -= tokens;
      return true;
    }
    putPenalty();
    return false;
  }

  private void refill() {
    long now = getCurrentTimeInSeconds();
    if ((now - lastRefillTimestamp) >= refillInSeconds) {
      currentBucketSize = maxBucketSize;
      lastRefillTimestamp = getCurrentTimeInSeconds();
    }
  }

  private void putPenalty() {
    lastFailedTimestamp = getCurrentTimeInSeconds();
  }

  private static long getCurrentTimeInSeconds() {
    return System.currentTimeMillis()/1000;
  }
}

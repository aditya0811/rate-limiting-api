public class Client {

  public static void main(String[] args)
      throws InterruptedException {

    TokenBucket tokenBucket = new TokenBucket(15, 60, 60);

    int numberOfRequests = 50;
    for(int i=0; i< numberOfRequests; i++) {
      if (tokenBucket.allowRequest(1)) {
        System.out.println("Request " + CallApi.call_me(String.valueOf(i)) + " is made at: "
            + getCurrentTimeInSeconds());

      } else {
        System.out.println("Request " + i + " is NOT called at: "
            + getCurrentTimeInSeconds());
        //Thread.sleep is not part of solution but, is used for assessment
        // of penalty, allowing enough time to invalidate the penalty.
        Thread.sleep(60000);

      }
    }

  }

  private static long getCurrentTimeInSeconds() {
    return System.currentTimeMillis()/1000;
  }
}

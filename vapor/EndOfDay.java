package vapor;

/**
 * A common interface for classes which need to perform some once-a-day
 * processing at the end of each day. Generally processing and resetting
 * once-a-day limits and buffers.
 */
public interface EndOfDay {
  /**
   * Process this day's buffers, reset daily limits.
   */
  void endDay();
}

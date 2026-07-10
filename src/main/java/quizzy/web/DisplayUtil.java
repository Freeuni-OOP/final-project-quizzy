package quizzy.web;

/**
 * Small display helpers shared across quiz JSPs.
 */
public final class DisplayUtil {

    private DisplayUtil() {
    }

    /**
     * Formats a duration in seconds as a human-readable string.
     *
     * @param seconds total seconds
     * @return e.g. "2m 34s", "59s", or "1h 2m"
     */
    public static String formatTime(long seconds) {
        if (seconds <= 0) {
            return "0s";
        }
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) {
            return h + "h " + m + "m";
        }
        if (m > 0) {
            return m + "m " + s + "s";
        }
        return s + "s";
    }
}

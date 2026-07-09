package quizzy.web;

/**
 * Static display-formatting helpers shared across JSPs. Kept separate from
 * WebEscape since this is about formatting values, not escaping user input.
 */
public final class DisplayUtil {

    private DisplayUtil() {
    }

    /** Formats a raw seconds count as "m:ss", e.g. 125 -> "2:05". */
    public static String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}

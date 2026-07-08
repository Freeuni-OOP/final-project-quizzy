package quizzy.tag;

import quizzy.dao.UserAchievementDAO;
import quizzy.model.Achievement;
import quizzy.model.User;
import quizzy.model.UserAchievement;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;

/**
 * Custom JSP tag that renders achievement icons for a given {@link User}.
 *
 * <p>Usage in JSP:
 * <pre>{@code
 *   <%@ taglib prefix="m4" uri="http://quizzy.freeuni.ge/tags/m4" %>
 *   <m4:achievements user="${currentUser}" />
 * }</pre>
 *
 * <p>Each achievement is rendered as a span with an emoji icon and a tooltip
 * describing the achievement.</p>
 */
public class AchievementTag extends SimpleTagSupport {

    private User user;

    /**
     * Sets the user whose achievements should be displayed.
     * Called by the JSP container via the TLD attribute definition.
     *
     * @param user the user to display achievements for
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Queries the user's achievements and writes the icon HTML to the page.
     */
    @Override
    public void doTag() throws JspException, IOException {
        if (user == null) {
            return;
        }

        UserAchievementDAO dao = new UserAchievementDAO();
        List<UserAchievement> achievements = dao.findByUser(user.getId());

        JspWriter out = getJspContext().getOut();
        for (UserAchievement ua : achievements) {
            Achievement a = ua.getAchievement();
            out.print("<span class=\"achievement-icon\" title=\"");
            out.print(escapeHtml(getDescription(a)));
            out.print("\">");
            out.print(getIcon(a));
            out.print("</span>");
        }
    }

    // -------------------------------------------------------------------
    // Achievement metadata
    // -------------------------------------------------------------------

    /**
     * Returns the emoji icon for the given achievement.
     */
    static String getIcon(Achievement achievement) {
        switch (achievement) {
            case AMATEUR_AUTHOR:
                return "📝";        // 📝
            case PROLIFIC_AUTHOR:
                return "✍️";        // ✍️
            case PRODIGIOUS_AUTHOR:
                return "🏆";        // 🏆
            case QUIZ_MACHINE:
                return "⚡";              // ⚡
            case I_AM_THE_GREATEST:
                return "👑";        // 👑
            case PRACTICE_MAKES_PERFECT:
                return "🎯";        // 🎯
            default:
                return "🏅";        // 🏅 (fallback)
        }
    }

    /**
     * Returns the human-readable description for the given achievement.
     */
    static String getDescription(Achievement achievement) {
        switch (achievement) {
            case AMATEUR_AUTHOR:
                return "Amateur Author: Created your first quiz!";
            case PROLIFIC_AUTHOR:
                return "Prolific Author: Created 5 quizzes!";
            case PRODIGIOUS_AUTHOR:
                return "Prodigious Author: Created 10 quizzes!";
            case QUIZ_MACHINE:
                return "Quiz Machine: Took 10 quizzes!";
            case I_AM_THE_GREATEST:
                return "I Am The Greatest: Got the highest score on a quiz!";
            case PRACTICE_MAKES_PERFECT:
                return "Practice Makes Perfect: Took a quiz in practice mode!";
            default:
                return achievement.name();
        }
    }

    /**
     * Escapes HTML special characters in a plain-text string so it is safe
     * to embed in an HTML attribute value.
     */
    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&#39;");
    }
}

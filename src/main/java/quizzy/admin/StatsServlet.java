package quizzy.admin;

import org.hibernate.Session;
import quizzy.util.HibernateUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* ---- HQL counts (Hibernate-managed entities) ---- */
        long totalUsers = countHql("SELECT COUNT(*) FROM User");
        long totalAnnouncements = countHql("SELECT COUNT(*) FROM Announcement");

        /* ---- Native SQL counts (non-entity tables) ---- */
        long totalQuizzes = countNative("SELECT COUNT(*) FROM quizzes");
        long totalAttempts = countNative("SELECT COUNT(*) FROM quiz_attempts");

        /* ---- Top 10 quizzes by attempt count ---- */
        List<StatRow> topByAttempts = topQuizzes(
                "SELECT q.title, COUNT(a.id) AS cnt "
                        + "FROM quiz_attempts a "
                        + "JOIN quizzes q ON a.quiz_id = q.id "
                        + "GROUP BY a.quiz_id, q.title "
                        + "ORDER BY cnt DESC",
                10);

        /* ---- Top 10 quizzes by average score ---- */
        List<StatRow> topByAvgScore = topQuizzes(
                "SELECT q.title, AVG(a.score) AS val "
                        + "FROM quiz_attempts a "
                        + "JOIN quizzes q ON a.quiz_id = q.id "
                        + "GROUP BY a.quiz_id, q.title "
                        + "ORDER BY val DESC",
                10);

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuizzes", totalQuizzes);
        request.setAttribute("totalAttempts", totalAttempts);
        request.setAttribute("totalAnnouncements", totalAnnouncements);
        request.setAttribute("topByAttempts", topByAttempts);
        request.setAttribute("topByAvgScore", topByAvgScore);

        request.getRequestDispatcher("/WEB-INF/admin/stats.jsp")
                .forward(request, response);
    }

    /* ---- Query helpers ---- */

    private long countHql(String hql) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (Long) session.createQuery(hql).uniqueResult();
        }
    }

    private long countNative(String sql) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Object result = session.createNativeQuery(sql).uniqueResult();
            return ((Number) result).longValue();
        }
    }

    private List<StatRow> topQuizzes(String sql, int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = session.createNativeQuery(sql)
                    .setMaxResults(limit)
                    .list();
            List<StatRow> result = new ArrayList<>();
            for (Object[] row : rows) {
                String title = (String) row[0];
                double value = ((Number) row[1]).doubleValue();
                result.add(new StatRow(title, value));
            }
            return result;
        }
    }

    /**
     * A single row in a top-N stats table.
     */
    public static class StatRow {
        private final String label;
        private final double value;

        public StatRow(String label, double value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public double getValue() {
            return value;
        }
    }
}

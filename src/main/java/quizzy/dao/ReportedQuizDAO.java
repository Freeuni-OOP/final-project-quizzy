package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.ReportStatus;
import quizzy.model.ReportedQuiz;

import java.util.List;

/**
 * Data Access Object for {@link ReportedQuiz} entities.
 */
public class ReportedQuizDAO extends BaseDAO<ReportedQuiz> {

    public ReportedQuizDAO() {
        super();
    }

    /**
     * Returns all reported quizzes filtered by their review status.
     *
     * @param status the report status to filter by
     * @return list of matching reports
     */
    public List<ReportedQuiz> findByStatus(ReportStatus status) {
        Session session = sessionFactory.openSession();
        try {
            Query<ReportedQuiz> query = session.createQuery(
                    "FROM ReportedQuiz WHERE status = :status ORDER BY createdAt ASC",
                    ReportedQuiz.class);
            query.setParameter("status", status);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Returns all reports filed against a specific quiz.
     *
     * @param quizId the quiz ID
     * @return list of reports for that quiz
     */
    public List<ReportedQuiz> findByQuiz(int quizId) {
        Session session = sessionFactory.openSession();
        try {
            Query<ReportedQuiz> query = session.createQuery(
                    "FROM ReportedQuiz WHERE quizId = :quizId ORDER BY createdAt DESC",
                    ReportedQuiz.class);
            query.setParameter("quizId", quizId);
            return query.list();
        } finally {
            session.close();
        }
    }
}

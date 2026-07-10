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

    // Returns all reported quizzes filtered by their review status.
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

    // Returns all reports filed against a specific quiz.
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

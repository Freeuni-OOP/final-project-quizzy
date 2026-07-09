package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.QuizAttempt;

import java.util.List;

public class QuizAttemptDAO extends BaseDAO<QuizAttempt> {

    public QuizAttemptDAO() {
        super();
    }

    public QuizAttempt findById(int id) {
        return super.findById(QuizAttempt.class, id);
    }

    public List<QuizAttempt> findAll() {
        return super.findAll(QuizAttempt.class);
    }

    public List<QuizAttempt> findByUser(int userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<QuizAttempt> query = session.createQuery(
                    "FROM QuizAttempt WHERE user.id = :userId ORDER BY id DESC",
                    QuizAttempt.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<QuizAttempt> findByQuiz(int quizId) {
        Session session = sessionFactory.openSession();
        try {
            Query<QuizAttempt> query = session.createQuery(
                    "FROM QuizAttempt WHERE quiz.id = :quizId ORDER BY score DESC, timeTakenSeconds ASC",
                    QuizAttempt.class);
            query.setParameter("quizId", quizId);
            return query.list();
        } finally {
            session.close();
        }
    }

    public Integer findMaxScoreByQuiz(int quizId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Integer> query = session.createQuery(
                    "SELECT MAX(score) FROM QuizAttempt WHERE quiz.id = :quizId",
                    Integer.class);
            query.setParameter("quizId", quizId);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}

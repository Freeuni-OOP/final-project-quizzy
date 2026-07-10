package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.Quiz;

import java.util.List;

public class QuizDAO extends BaseDAO<Quiz> {

    public QuizDAO() {
        super();
    }

    public Quiz findById(int id) {
        return super.findById(Quiz.class, id);
    }

    public List<Quiz> findAll() {
        return super.findAll(Quiz.class);
    }

    public List<Quiz> findByCreator(int userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Quiz> query = session.createQuery(
                    "FROM Quiz WHERE creator.id = :userId ORDER BY id DESC",
                    Quiz.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<Quiz> findPopular(int limit) {
        Session session = sessionFactory.openSession();
        try {
            Query<Quiz> query = session.createQuery(
                    "SELECT a.quiz FROM QuizAttempt a GROUP BY a.quiz ORDER BY COUNT(a) DESC",
                    Quiz.class);
            query.setMaxResults(limit);
            return query.list();
        } finally {
            session.close();
        }
    }
}

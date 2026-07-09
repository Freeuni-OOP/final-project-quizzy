package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.question.Question;

import java.util.List;

public class QuestionDAO extends BaseDAO<Question> {

    public QuestionDAO() {
        super();
    }

    public Question findById(int id) {
        return super.findById(Question.class, id);
    }

    public List<Question> findAll() {
        return super.findAll(Question.class);
    }

    public List<Question> findByQuiz(int quizId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Question> query = session.createQuery(
                    "FROM Question WHERE quiz.id = :quizId ORDER BY questionOrder ASC",
                    Question.class);
            query.setParameter("quizId", quizId);
            return query.list();
        } finally {
            session.close();
        }
    }
}

package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import quizzy.util.HibernateUtil;

import java.util.List;

/**
 * Generic Data Access Object providing common CRUD operations for any entity type.
 * Specific DAOs extend this class to inherit standard operations and add custom queries.
 *
 * @param <T> the entity type managed by this DAO
 */
public class BaseDAO<T> {

    protected final SessionFactory sessionFactory;

    public BaseDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Persists a new entity to the database.
     *
     * @param entity the entity to save
     */
    public void save(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Retrieves an entity by its primary key.
     *
     * @param clazz the entity class
     * @param id    the primary key value
     * @return the entity, or {@code null} if not found
     */
    public T findById(Class<T> clazz, int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(clazz, id);
        } finally {
            session.close();
        }
    }

    /**
     * Retrieves all entities of the given type.
     *
     * @param clazz the entity class
     * @return a list of all entities
     */
    public List<T> findAll(Class<T> clazz) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM " + clazz.getSimpleName(), clazz).list();
        } finally {
            session.close();
        }
    }

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update (must have a valid ID)
     */
    public void update(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity the entity to delete (must have a valid ID)
     */
    public void delete(T entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
}

package org.example.dao;

import org.example.config.HibernateConfig;
import org.example.exception.DatabaseException;
import org.example.exception.DuplicateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.JDBCConnectionException;

import java.util.List;

/**
 * Abstract implementation of {@link CrudDao} for database entities using Hibernate.
 * <p>
 * Provides general CRUD operations (create, read, update, delete) for any entity {@code E}.
 * <p>
 * All methods wrap low-level Hibernate/PostgreSQL exceptions into:
 * <ul>
 *     <li>{@link DuplicateException} — for unique constraint violations (e.g., duplicate email)</li>
 *     <li>{@link DatabaseException} — for connection errors or other unexpected database errors</li>
 * </ul>
 * <p>
 * Subclasses must specify the entity type via the {@code eClass} field.
 *
 * @param <E> type of the entity
 */
public abstract class CrudDaoImpl<E> implements CrudDao<E> {

    /**
     * Hibernate {@link SessionFactory} for database access.
     */
    protected final SessionFactory sessionFactory;

    /**
     * Entity class for Hibernate queries.
     */
    protected Class<E> eClass;

    /**
     * Constructor. Initializes {@link SessionFactory} from {@link HibernateConfig}.
     */
    public CrudDaoImpl() {
        sessionFactory = HibernateConfig.getSessionFactory();
    }

    /**
     * Returns all entities of type {@code E}.
     *
     * @return list of all entities
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public List<E> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + eClass.getSimpleName(), eClass).list();
        }
    }

    /**
     * Returns an entity by its ID.
     *
     * @param id entity ID
     * @return entity or {@code null} if not found
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public E getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(eClass, id);
        }
    }

    /**
     * Saves a new entity to the database.
     *
     * @param e entity to save
     * @throws DuplicateException if unique constraints are violated
     * @throws DatabaseException  if a database error occurs
     */
    @Override
    public void save(E e) {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.save(e);
            t.commit();
        } catch (Exception ex) {
            rollback(t);

            if (isConstraintViolation(ex)) {
                throw new DuplicateException("Unique constraint violation during save", ex);
            }

            if (ex instanceof JDBCConnectionException || ex.getCause() instanceof JDBCConnectionException) {
                throw new DatabaseException("Database connection error", ex);
            }

            throw new DatabaseException("Unexpected database error during save", ex);
        }
    }

    /**
     * Updates an existing entity in the database.
     *
     * @param e entity to update
     * @throws DuplicateException if unique constraints are violated
     * @throws DatabaseException  if a database error occurs
     */
    @Override
    public void update(E e) {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            session.update(e);
            t.commit();
        } catch (Exception ex) {

            if (isConstraintViolation(ex)) {
                throw new DuplicateException("Unique constraint violation during update", ex);
            }

            if (ex instanceof JDBCConnectionException || ex.getCause() instanceof JDBCConnectionException) {
                throw new DatabaseException("Database connection error", ex);
            }

            throw new DatabaseException("Unexpected database error during update", ex);
        }
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id entity ID
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public void delete(Long id) {
        Transaction t = null;
        try (Session session = sessionFactory.openSession()) {
            t = session.beginTransaction();
            E e = session.get(eClass, id);
            if (e == null) {
                throw new DatabaseException("User not found with id: " + id);
            }
            session.delete(e);
            t.commit();
        } catch (DatabaseException ex) {
            throw new DatabaseException("User not found with id: " + id);
        } catch (JDBCConnectionException ex) {
            rollback(t);
            throw new DatabaseException("Database connection error", ex);
        } catch (Exception ex) {
            rollback(t);
            throw new DatabaseException("Unexpected database error during delete", ex);
        }
    }

    /**
     * Rolls back the transaction if it is active.
     *
     * @param t transaction to rollback
     */
    private void rollback(Transaction t) {
        if (t != null) {
            t.rollback();
        }
    }

    /**
     * Checks if the provided exception or any of its causes is a
     * {@link org.hibernate.exception.ConstraintViolationException}.
     * <p>
     * This helper method performs a recursive check down the exception chain.
     * This is necessary because Hibernate often wraps low-level database
     * constraint violations (like unique constraint failures) into higher-level
     * exceptions such as {@code PersistenceException} or {@code RollbackException}.
     * </p>
     *
     * @param ex the exception caught during a database operation.
     * @return {@code true} if a constraint violation is found in the stack trace;
     * {@code false} otherwise.
     */
    private boolean isConstraintViolation(Exception ex) {
        return ex instanceof org.hibernate.exception.ConstraintViolationException
                || ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException
                || (ex.getCause() != null && ex.getCause().getCause() instanceof org.hibernate.exception.ConstraintViolationException);
    }
}
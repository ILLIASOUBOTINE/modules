package org.example.dao;

import java.util.List;

/**
 * Generic CRUD DAO interface.
 *
 * @param <E> entity type
 */
public interface CrudDao<E> {

    /**
     * Returns all entities.
     */
    List<E> getAll();

    /**
     * Finds entity by id.
     */
    E getById(Long id);

    /**
     * Saves entity.
     */
    void save(E e);

    /**
     * Updates entity.
     */
    void update(E e);

    /**
     * Deletes entity by id.
     */
    void delete(Long id);
}

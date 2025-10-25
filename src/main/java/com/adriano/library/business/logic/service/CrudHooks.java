package com.adriano.library.business.logic.service;

public interface CrudHooks<T, ID> {
    // Default no-op implementations of hooks (override only if needed)

    default void beforeSave(T entity) {

    }

    default void afterSave(T entity) {

    }

    default void beforeRead(ID id) {

    }

    default void afterRead(T entity) {

    }

    default void beforeUpdate(ID id, T entity) {

    }

    default void afterUpdate(T entity) {

    }

    default void beforeDelete(ID id) {

    }

    default void afterDelete(ID id) {

    }
}
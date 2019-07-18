package de.htw.ai.web.askmystudents.service;

import java.util.List;

public interface IService<T, C, P> {

    /**
     * @param id from parent object
     * @return all objects that has the given parent id
     */
    List<T> getAllFromParent(Long id);

//    List<T> getAllFromUser(Long id);

    T insert(T t);

    T getById(Long id);

    T update(T t);

    /**
     * @param id from object T
     * @return returns all child objects that object T has
     */
    List<C> getObjects(Long id);

    T addChildObject(Long id, C c);

    T addParentObject(Long id, P p);

    T deleteChildObject(Long id, Long childId);

    void delete(Long id);

    T duplicate(Long id, boolean withParent);
}

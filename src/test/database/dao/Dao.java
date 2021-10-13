package test.database.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    /*
     * мы исключаем привязку к сущности и id
     * поэтому параметризуем интерфейс*/

    List<E> findAll();

    Optional<E> findById(K id);

    void update(E user);

    boolean delete(K id);

    E save(E user);


}

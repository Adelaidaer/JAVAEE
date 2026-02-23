package org.kursach.kursach.repository;

import org.kursach.kursach.model.Person;

import java.util.List;

/**
 * Репозиторий для управления сущностями Person
 */
public interface PersonRepository extends Repository<Person, Long> {
    
    /**
     * Найти лица по фрагменту шифра
     * @param shiffer фрагмент шифра для поиска
     * @return список найденных лиц
     */
    List<Person> findByShiffer(String shiffer);
    
    /**
     * Найти лицо по ИНН
     * @param inn ИНН лица
     * @return найденное лицо или null
     */
    Person findByInn(String inn);
    
    /**
     * Найти лица по типу
     * @param type тип лица
     * @return список найденных лиц
     */
    List<Person> findByType(String type);
} 
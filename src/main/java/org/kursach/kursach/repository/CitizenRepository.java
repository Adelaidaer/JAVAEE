package org.kursach.kursach.repository;

import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Person;

import java.util.List;

/**
 * Репозиторий для управления сущностями Citizen
 */
public interface CitizenRepository extends Repository<Citizen, Long> {
    
    /**
     * Найти граждан по фрагменту имени
     * @param name фрагмент имени для поиска
     * @return список найденных граждан
     */
    List<Citizen> findByName(String name);
    
    /**
     * Найти гражданина по персональному номеру
     * @param personalNumber персональный номер гражданина
     * @return найденный гражданин или null
     */
    Citizen findByPersonalNumber(String personalNumber);
    
    /**
     * Найти граждан по связанному лицу
     * @param person лицо
     * @return список граждан связанных с лицом
     */
    List<Citizen> findByPerson(Person person);
} 
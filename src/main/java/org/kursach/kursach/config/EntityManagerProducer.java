package org.kursach.kursach.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.logging.Logger;

@ApplicationScoped
public class EntityManagerProducer {

    private static final Logger logger = Logger.getLogger(EntityManagerProducer.class.getName());
    private static EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        try {
            logger.info("Инициализация EntityManagerFactory...");
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("curriculumPU");
                logger.info("EntityManagerFactory успешно создан");
            }
        } catch (Exception e) {
            logger.severe("Ошибка создания EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось создать EntityManagerFactory", e);
        }
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        try {
            logger.info("Создание EntityManager...");
            if (emf == null) {
                logger.warning("EntityManagerFactory равен null, попытка повторной инициализации");
                init();
            }
            EntityManager em = emf.createEntityManager();
            logger.info("EntityManager успешно создан");
            return em;
        } catch (Exception e) {
            logger.severe("Ошибка создания EntityManager: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось создать EntityManager", e);
        }
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

    @PreDestroy
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
} 
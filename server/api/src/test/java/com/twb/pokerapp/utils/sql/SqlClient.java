package com.twb.pokerapp.utils.sql;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SqlClient implements AutoCloseable {
    private static final String PERSISTENCE_UNIT_NAME = "poker-app-test";
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public SqlClient(JdbcDatabaseContainer<?> container) {
        this(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    private SqlClient(String url, String username, String password) {
        var properties = new HashMap<String, String>();
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", username);
        properties.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        em = emf.createEntityManager();
    }

    // *****************************************************************************************
    // Get All
    // *****************************************************************************************

    public List<PokerTable> getPokerTables() {
        return getAll(PokerTable.class);
    }

    // *****************************************************************************************
    // Get By ID
    // *****************************************************************************************

    public Optional<PlayerSession> getPlayerSession(UUID id) {
        return getById(id, PlayerSession.class);
    }

    public Optional<AppUser> getAppUser(UUID id) {
        return getById(id, AppUser.class);
    }

    public Optional<PokerTable> getPokerTable(UUID id) {
        return getById(id, PokerTable.class);
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private <T> Optional<T> getById(UUID id, Class<T> clazz) {
        try {
            var className = clazz.getSimpleName();
            var query = "SELECT o FROM " + className + " o WHERE o.id = :id";
            return Optional.of(em.createQuery(query, clazz)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private <T> List<T> getAll(Class<T> clazz) {
        var className = clazz.getSimpleName();
        var query = "SELECT o FROM " + className + " o";
        return em.createQuery(query, clazz).getResultList();
    }

    // *****************************************************************************************
    // Lifecycle
    // *****************************************************************************************

    @Override
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

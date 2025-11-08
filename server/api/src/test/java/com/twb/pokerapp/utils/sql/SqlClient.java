package com.twb.pokerapp.utils.sql;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SqlClient implements AutoCloseable {
    private static final String PERSISTENCE_UNIT_NAME = "poker-app-test";
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public SqlClient(JdbcDatabaseContainer container) {
        this(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    private SqlClient(String url, String username, String password) {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", username);
        properties.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        em = emf.createEntityManager();
    }

    public Optional<PlayerSession> getPlayerSession(UUID playerSessionId) {
        try {
            return Optional.of(em.createQuery("SELECT ps FROM PlayerSession ps WHERE ps.id = :id", PlayerSession.class)
                    .setParameter("id", playerSessionId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }




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

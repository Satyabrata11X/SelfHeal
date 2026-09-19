package com.selfheal.starter.persistence;

import com.selfheal.starter.incident.FailureContext;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public class SelfHealPersistenceManager
        implements FailureIncidentPersistence {

    private final FailureIncidentPersistence delegate;

    private final String providerName;

    public SelfHealPersistenceManager(
            FailureIncidentPersistence delegate,
            String providerName) {

        if (delegate == null) {
            throw new IllegalArgumentException(
                    "Persistence delegate cannot be null"
            );
        }

        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Persistence provider name cannot be empty"
            );
        }

        this.delegate = delegate;
        this.providerName = providerName;
    }

    @Override
    public void save(FailureContext context) {
        delegate.save(context);
    }

    @Override
    public List<FailureContext> findAll() {
        return delegate.findAll();
    }

    @Override
    public void deleteAll() {
        delegate.deleteAll();
    }

    public String getProviderName() {
        return providerName;
    }
}
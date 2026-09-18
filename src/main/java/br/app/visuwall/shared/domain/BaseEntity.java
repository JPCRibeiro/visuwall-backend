package br.app.visuwall.shared.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity implements Persistable<UUID> {
    @Id
    protected UUID id;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    protected void assignId() {
        this.id = UuidCreator.getTimeOrderedEpoch();
    }
}
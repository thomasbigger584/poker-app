package com.twb.pokerapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "temperament")
public class Temperament extends Auditable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "from_roll")
    private Float fromRoll;

    @NotNull
    @Column(name = "to_roll")
    private Float toRoll;

    @Column(name = "modifier")
    private String modifier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temperament temperament = (Temperament) o;
        return new EqualsBuilder().append(id, temperament.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "Temperament{" +
                "id=" + id +
                ", fromRoll=" + fromRoll +
                ", toRoll=" + toRoll +
                '}';
    }
}

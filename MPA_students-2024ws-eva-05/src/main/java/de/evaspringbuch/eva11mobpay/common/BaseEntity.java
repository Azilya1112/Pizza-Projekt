package de.evaspringbuch.eva11mobpay.common;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable> {

	@Id
    @GeneratedValue // (strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    private PK id;

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
    	return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {   	
        if (this == obj) return true;
    	if (obj == null || getClass() != obj.getClass()) return false;
    	BaseEntity<?> other = (BaseEntity<?>) obj;
    	return this.getId() != null && this.getId().equals(other.getId());  	
    }


}

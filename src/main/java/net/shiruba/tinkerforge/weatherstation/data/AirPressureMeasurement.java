package net.shiruba.tinkerforge.weatherstation.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class AirPressureMeasurement {

    @Id
    private String id;

    private Integer airPressure;

    @CreatedDate
    private DateTime createdDate;

    public AirPressureMeasurement(Integer airPressure) {
        this.airPressure = airPressure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(Integer airPressure) {
        this.airPressure = airPressure;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AirPressureMeasurement rhs = (AirPressureMeasurement) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.airPressure, rhs.airPressure)
                .append(this.createdDate, rhs.createdDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(airPressure)
                .append(createdDate)
                .toHashCode();
    }
}

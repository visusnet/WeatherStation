package net.shiruba.tinkerforge.weatherstation.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class TemperatureMeasurement {

    @Id
    private String id;

    private int temperature;

    @CreatedDate
    private DateTime createdDate;

    public TemperatureMeasurement(int temperature) {
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
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
        TemperatureMeasurement rhs = (TemperatureMeasurement) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.temperature, rhs.temperature)
                .append(this.createdDate, rhs.createdDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(temperature)
                .append(createdDate)
                .toHashCode();
    }
}

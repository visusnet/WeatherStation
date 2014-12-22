package net.shiruba.tinkerforge.weatherstation.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class HumidityMeasurement {

    @Id
    private String id;

    private Integer humidity;

    @CreatedDate
    private DateTime createdDate;

    public HumidityMeasurement(Integer humidity) {
        this.humidity = humidity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
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
        HumidityMeasurement rhs = (HumidityMeasurement) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.humidity, rhs.humidity)
                .append(this.createdDate, rhs.createdDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(humidity)
                .append(createdDate)
                .toHashCode();
    }
}

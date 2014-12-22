package net.shiruba.tinkerforge.weatherstation.data;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public class IlluminanceMeasurement {

    @Id
    private String id;

    private Integer illuminance;

    @CreatedDate
    private DateTime createdDate;

    public IlluminanceMeasurement(Integer illuminance) {
        this.illuminance = illuminance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIlluminance() {
        return illuminance;
    }

    public void setIlluminance(Integer illuminance) {
        this.illuminance = illuminance;
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
        IlluminanceMeasurement rhs = (IlluminanceMeasurement) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.illuminance, rhs.illuminance)
                .append(this.createdDate, rhs.createdDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(illuminance)
                .append(createdDate)
                .toHashCode();
    }
}

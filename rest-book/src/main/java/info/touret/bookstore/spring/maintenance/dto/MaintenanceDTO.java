package info.touret.bookstore.spring.maintenance.dto;

import java.io.Serializable;
import java.util.Date;

public class MaintenanceDTO implements Serializable {
    private boolean isInMaintenance = false;
    private Date from;

    public MaintenanceDTO(boolean isInMaintenance, Date from) {
        this.isInMaintenance = isInMaintenance;
        this.from = from;
    }

    public MaintenanceDTO() {
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    public void setInMaintenance(boolean inMaintenance) {
        isInMaintenance = inMaintenance;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }
}

package iwp.crawler.iwpcrawler.bean;

public class SlaveDepartement {

    private String departement;
    private String slave;


    public SlaveDepartement(String departement, String slave) {
        this.departement = departement;
        this.slave = slave;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getSlave() {
        return slave;
    }

    public void setSlave(String slave) {
        this.slave = slave;
    }
}

package example.com.rcremote;

/**
 * Created by leul on 4/3/16.
 */
public class DeviceItem {

    private String dname;
    private String daddr;
    private boolean dconn;

    public String getName(){
        return dname;
    }

    public String getAddress(){
        return daddr;
    }

    public boolean getConnected(){
        return dconn;
    }

    public void setName(String name){
        dname = name;
    }

    public DeviceItem(String name, String address, String connected){
        this.dname = name;
        this.daddr = address;
        if(connected == "true")
            this.dconn = true;

        else
            this.dconn = false;
    }
}

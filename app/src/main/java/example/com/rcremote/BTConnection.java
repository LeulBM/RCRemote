package example.com.rcremote;

/**
 * Created by leul on 4/3/16.
 */
public class BTConnection {
    private String name;
    private String addr;
    private boolean status;

    public BTConnection(String n, String a, boolean s){
        name = n;
        addr = a;
        status = s;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return addr;
    }

    public boolean getStatus(){
        return status;
    }

    public void setName(String n){
        name = n;
    }

    public void setAddress(String a){
        addr = a;
    }

    public void setStatus(boolean s){
        status = s;
    }
}

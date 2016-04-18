package bean;

/**
 * Created by Administrator on 2016/4/12.
 */
public class LoginMessage {

    private String username;
    private int user_id;

    public String getUsername() {
        return username;
    }

    public int getUserID(){
        return user_id;
    }

    public LoginMessage(String username,int user_id){
        this.username = username;
        this.user_id = user_id;
    }
}

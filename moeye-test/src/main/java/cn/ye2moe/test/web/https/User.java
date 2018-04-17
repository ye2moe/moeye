package cn.ye2moe.test.web.https;

public class User {
    private int userType;

    private String username;

    private int colleageId;


    public User() {
    }

    public User(String username, int colleageId) {
        this.userType = 0;
        this.username = username;
        this.colleageId = colleageId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getColleageId() {
        return colleageId;
    }

    public void setColleageId(int colleageId) {
        this.colleageId = colleageId;
    }
}

package cn.ye2moe.test.web.https;

public class CurrentCourse {
    String id;
    String name;

    public CurrentCourse(Parse parse) {
        id = parse.jsonRegExp("id");
        name = parse.jsonRegExp("courseName");
    }

    public CurrentCourse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "CurrentCourse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package cl.playground.parser;

public class JavaGen {
    private String pkg;
    private String entityPackage;
    private String out;

    public String getPackage() {
        return pkg;
    }

    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    @Override
    public String toString() {
        return "JavaGen{" +
                "pkg='" + pkg + '\'' +
                ", entityPackage='" + entityPackage + '\'' +
                ", out='" + out + '\'' +
                '}';
    }
}

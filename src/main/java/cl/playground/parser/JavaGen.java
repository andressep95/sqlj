package cl.playground.parser;

public class JavaGen {
    private String pkg;
    private String entityPackage;
    private String out;
    private String typeRepository;

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

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getTypeRepository() {
        return typeRepository;
    }

    public void setTypeRepository(String typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public String toString() {
        return "JavaGen{" +
                "pkg='" + pkg + '\'' +
                ", entityPackage='" + entityPackage + '\'' +
                ", out='" + out + '\'' +
                ", typeRepository='" + typeRepository + '\'' +
                '}';
    }
}
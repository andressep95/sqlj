package cl.playground.parser;

public class Gen {
    private JavaGen java;

    public JavaGen getJava() {
        return java;
    }

    public void setJava(JavaGen java) {
        this.java = java;
    }

    @Override
    public String toString() {
        return "Gen{" +
                "java=" + java +
                '}';
    }
}

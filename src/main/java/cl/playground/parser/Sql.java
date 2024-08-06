package cl.playground.parser;

public class Sql {
    private String engine;
    private Gen gen;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Gen getGen() {
        return gen;
    }

    public void setGen(Gen gen) {
        this.gen = gen;
    }

    @Override
    public String toString() {
        return "Sql{" +
                "engine='" + engine + '\'' +
                ", gen=" + gen +
                '}';
    }
}

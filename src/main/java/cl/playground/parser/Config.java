package cl.playground.parser;

import java.util.List;

public class Config {
    private String version;
    private List<Sql> sql;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Sql> getSql() {
        return sql;
    }

    public void setSql(List<Sql> sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "Config{" +
                "version='" + version + '\'' +
                ", sql=" + sql +
                '}';
    }
}

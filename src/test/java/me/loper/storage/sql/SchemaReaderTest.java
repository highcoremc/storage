package me.loper.storage.sql;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SchemaReaderTest extends TestCase {
    private final List<String> queries = new ArrayList<>();

    @Override
    public void setUp() {
        this.queries.add("create table if not exists \"users\" ( \"id\" serial primary key not null )");
        this.queries.add("create table if not exists \"user_password\" ( \"id\" serial primary key not null )");
        this.queries.add("drop trigger if exists some_table_update_date on \"user_password\"");
        this.queries.add("create or replace function update_modified_column() returns trigger as $$ begin if row (new.*) is distinct from row (old.*) then new.updated_at = now(); return new; else return old; end if; end; $$ language 'plpgsql'");
        this.queries.add("create trigger some_table_update_date before update on \"user_password\" for row execute procedure update_modified_column()");
    }

    @Test
    public void testPostgresqlRead() throws IOException {
        if (0 == this.queries.size()) {
            return;
        }

        InputStream stream = this.readFromFile("postgre.sql");
        List<String> parsedValues = SchemaReader.getStatements(stream);

        for (int i = 0; i < parsedValues.size(); i++) {
            try {
                Assertions.assertEquals(this.queries.get(i), parsedValues.get(i));
            } catch (IndexOutOfBoundsException ignored) {
                Assertions.assertEquals("Not implemented", parsedValues.get(i));
            }
        }
    }

    private InputStream readFromFile(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

}

package me.loper.storage.sql;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public final class SchemaReader {
    private SchemaReader() {}

    public static List<String> getStatements(InputStream is) throws IOException {
        Preconditions.checkArgument(is != null, "SchemaReader input stream can not be null.");

        List<String> queries = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                sb.append(" ").append(line.trim().toLowerCase());

                // only used for PostgreSQL
                if (
                    StringUtils.contains(sb, "create")
                    && StringUtils.contains(sb, "function")
                    && StringUtils.contains(sb, "begin")
                    && !StringUtils.contains(sb, "language")
                ) {
                    continue;
                }

                // check for end of declaration
                if (line.endsWith(";")) {
                    sb.deleteCharAt(sb.length() - 1);

                    String result = sb.toString().trim();
                    if (!result.isEmpty()) {
                        queries.add(result);
                    }

                    // reset
                    sb = new StringBuilder();
                }
            }
        }

        return queries;
    }
}

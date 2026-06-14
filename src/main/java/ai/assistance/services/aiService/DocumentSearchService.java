package ai.assistance.services.aiService;

import ai.assistance.searchRecord.RankedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // full text search with rank
    public List<RankedResult> fullTextSearchWithRank(String query) {

        String sql = """
        SELECT content,
               ts_rank(
                   to_tsvector('english', content),
                   plainto_tsquery('english', ?)
               ) AS rank
        FROM vector_store
        WHERE to_tsvector('english', content)
              @@ plainto_tsquery('english', ?)
        ORDER BY rank DESC LIMIT 10
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new RankedResult(
                        rs.getString("content"),
                        rs.getDouble("rank")
                ),
                query,
                query
        );
    }
}

package com.tc.reptile.dao.jdbc;

import java.util.List;
import java.util.Map;

public interface ArticleTypeInfoJdbcDao {
    List<Map<String, Object>> countType(Long webId);
}

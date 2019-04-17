package com.tc.reptile.dao.jdbc;

import com.tc.reptile.model.GameChangeDTO;
import com.tc.reptile.model.GameCountDTO;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameAppearRecordJdbcDao {
    List<GameChangeDTO> gameCount(long startDare, long endDate);
    List<GameCountDTO> gameCount();
}

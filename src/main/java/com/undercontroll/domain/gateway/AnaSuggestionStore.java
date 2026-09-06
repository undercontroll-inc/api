package com.undercontroll.domain.gateway;

import java.util.List;
import java.util.Optional;

public interface AnaSuggestionStore {

    Optional<List<String>> findByUserId(Integer userId);

    void save(Integer userId, List<String> suggestions);
}

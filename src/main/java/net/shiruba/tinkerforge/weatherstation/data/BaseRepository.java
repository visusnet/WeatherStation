package net.shiruba.tinkerforge.weatherstation.data;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T> extends MongoRepository<T, String> {

    T findFirstByOrderByIdDesc();
    List<T> findFirst20ByOrderByIdDesc();
}

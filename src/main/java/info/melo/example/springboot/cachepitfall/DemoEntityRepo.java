package info.melo.example.springboot.cachepitfall;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class DemoEntityRepo {

    @Cacheable("entities")
    public DemoEntity getByIdCached (int id) {
        System.out.println("executed @Cacheable method");
        return new DemoEntity(id, "Some entity");
    }

    public DemoEntity getByIdNoCachingPossible (int id) {
        return getByIdCached(id);
    }
}

package info.melo.example.springboot.cachepitfall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private DemoEntityRepo repo;

    @Autowired
    public DemoService(DemoEntityRepo repo) {
        this.repo = repo;
    }

    public DemoEntity getEntityByIdCached(int id) {
        return repo.getByIdCached(id);
    }

    public DemoEntity getEntityByIdNoCachingPossible(int id) {
        return repo.getByIdNoCachingPossible(id);
    }
}

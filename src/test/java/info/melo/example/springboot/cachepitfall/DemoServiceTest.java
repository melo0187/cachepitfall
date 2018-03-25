package info.melo.example.springboot.cachepitfall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoServiceTest {

    @Autowired
    private DemoService service;

    @Autowired
    private DemoEntityRepo wiredRepo;

    @Spy
    private DemoEntityRepo spiedRepo;

    @Test
    public void shouldInvokeRepoMethodOnce_whenGettingTwice_canNotCountInvocationsOnRepo() {

        service.getEntityByIdCached(1);
        service.getEntityByIdCached(1);

        /*
         * This test does prettty much what I want,
         * BUT I can't figure out how to verify that
         * the method has been invoked only once because of @Cacheable.
         * I don't have access to the repo and even if I had,
         * I can't pass it as argument to Mockito.verify().
         *
         */
        //Mockito.verify(missingRepo, Mockito.times(1)).getByIdCached(2);
    }

    @Test
    public void shouldInvokeRepoMethodOnce_whenGettingTwice_canNotMockitoVerifyRepoIfNotMocked() {
        service = new DemoService(wiredRepo);

        service.getEntityByIdCached(3);
        service.getEntityByIdCached(3);

        /*
         * This is an attempt to get a hold on the repo
         * that is used for the service, so I can verify it.
         * This test again does what I actually want.
         * However I can't pass this object to Mockito.verify().
         */
        Mockito.verify(wiredRepo, Mockito.times(1)).getByIdCached(3);
    }

    @Test
    public void shouldInvokeRepoMethodOnce_whenGettingTwice_spyDoesSeemToCircumventCacheableAnnotation() {
        service = new DemoService(spiedRepo);

        service.getEntityByIdCached(2);
        service.getEntityByIdCached(2);

        /*
         * Does seem to bypass the context's cache definition,
         * since the "real" repo is used, but method invoked twice.
         */
        Mockito.verify(spiedRepo, Mockito.times(1)).getByIdCached(2);
    }

    @Test
    public void shouldReturnSameCachedEntityObject_whenGettingBySameIdTwice() {

        /*
         * This test uses another verification approach.
         * Rather than verifying invocation count on the repo method
         * I verify that the same objects is returned when getting from repo with unchanged parameters,
         * implying that this means they come from cache and not from the repo.
         */
        DemoEntity firstFetchedEntity = service.getEntityByIdCached(1);

        DemoEntity secondFetchedEntity = service.getEntityByIdCached(1);
        assertThat(secondFetchedEntity, is(firstFetchedEntity));
    }

    @Test
    public void shouldReturnDifferentEntityObjects_whenGettingBySameIdTwice_becauseCircumventingCache() {

        /*
         * This test is the cross check for shouldReturnSameCachedEntityObject_whenGettingBySameIdTwice
         * and what I actually wanted to demonstrate with the tests.
         * If you forget @Cacheable's default behavior and call a method with that annotation from within the class
         * you actually circumvent the cache proxy
         */
        DemoEntity firstFetchedEntity = service.getEntityByIdNoCachingPossible(1);

        DemoEntity secondFetchedEntity = service.getEntityByIdNoCachingPossible(1);
        assertThat(secondFetchedEntity, not(firstFetchedEntity));
    }
}
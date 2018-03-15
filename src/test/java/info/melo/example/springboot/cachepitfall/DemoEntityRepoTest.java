package info.melo.example.springboot.cachepitfall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DemoEntityRepoTest {

    /*
     * This is based on https://stackoverflow.com/questions/24221569/how-to-test-springs-declarative-caching-support-on-spring-data-repositories/24229350#24229350
     * a response from Oliver Gierke on that topic.
     * I might be misunderstanding his answer.
     * The way I understand it, I strongly dislike it.
     * First of all I don't want to test a fake test context.
     * Second of all, it does not actually work, the tests below are useless.
     *
     * I might want to follow along with this instead: https://stackoverflow.com/a/24469067
     */
    @Configuration
    @EnableCaching
    static class Config {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("entities");
        }

        @Bean
        DemoEntityRepo myRepo() {
            return Mockito.mock(DemoEntityRepo.class);
        }
    }

    @Autowired
    CacheManager manager;

    @Autowired
    DemoEntityRepo repo;

    @Test
    public void shouldInvokeRepoMethodOnce_whenGettingTwice() {

        repo.getByIdCached(1);
        repo.getByIdCached(1);

        /*
         * This test is broken. It passes no matter the number
         * of invocation counts we assert...
         */

        verify(repo, times(15)).getByIdCached(1);
    }

    @Test
    public void shouldInvokeRepoMethodOnce_butCanNotDoSoBecauseWeCircumventProxy() {

        Mockito.reset(repo);

        repo.getByIdNoCachingPossible(2);
        repo.getByIdNoCachingPossible(2);


        /*
         * Something is wrong here!
         * If executed alone this test passes, which it shouldn't!
         * The whole purpose of this repo is to find a way to catch
         * the mistake that circumvents use of a proxy when calling
         * a @Cacheable annotated method from within the same class.
         *
         * To further confuse me when this test is executed in one go
         * with the other test above, it throws UnfinishedVerificationException
         */
        verify(repo, times(1)).getByIdNoCachingPossible(2);
    }
}
package edu.iis.mto.blog.domain.repository;

    import edu.iis.mto.blog.domain.model.AccountStatus;
    import edu.iis.mto.blog.domain.model.BlogPost;
    import edu.iis.mto.blog.domain.model.LikePost;
    import edu.iis.mto.blog.domain.model.User;
    import org.junit.Assert;
    import org.junit.Before;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
    import org.springframework.test.context.junit4.SpringRunner;

    import static org.hamcrest.MatcherAssert.assertThat;
    import static org.hamcrest.Matchers.*;
    import static org.hamcrest.Matchers.not;
    import static org.hamcrest.Matchers.notNullValue;


@RunWith(SpringRunner.class)
@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    private LikePostRepository likePostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private BlogPost blogPost;
    private User firstUser;
    private User secondUser;

    @Before
    public void setUp() throws Exception {
        firstUser = userRepository.findOne(1L);
        secondUser = new User();
        secondUser.setFirstName("Jan");
        secondUser.setLastName("Kowalski");
        secondUser.setEmail("jankowalski@domain.com");
        secondUser.setAccountStatus(AccountStatus.NEW);
        secondUser = userRepository.save(secondUser);
        blogPost = new BlogPost();
        blogPost.setUser(firstUser);
        blogPost.setEntry("Tresc posta");
        blogPost = blogPostRepository.save(blogPost);
    }

    private LikePost giveLike(User user){
        LikePost likePost = new LikePost();
        likePost.setUser(user);
        likePost.setPost(blogPost);
        return likePostRepository.save(likePost);
    }

    @Test
    public void shouldStoreNewLike() throws Exception {
        LikePost likePost = giveLike(secondUser);
        Assert.assertThat(likePost.getId(), notNullValue());
    }

    @Test
    public void shouldPostHaveLike() throws Exception {
        LikePost likePost = giveLike(secondUser);
        testEntityManager.refresh(blogPost);
        assertThat(blogPost.getLikes(), not(empty()));
        assertThat(blogPost.getLikes().get(0).getId(), is(likePost.getId()));
    }

    @Test
    public void shouldPostBeEdited() throws Exception {
        LikePost likePost = giveLike(secondUser);
        likePost.setUser(firstUser);
        likePostRepository.save(likePost);
        testEntityManager.refresh(blogPost);
        assertThat(blogPost.getLikes().get(0).getUser().getId(), is(firstUser.getId()));
    }

    @Test
    public void shouldFindByUserAndPost() throws Exception {
        LikePost likePost = giveLike(secondUser);
        LikePost likePost1 = likePostRepository.findByUserAndPost(secondUser, blogPost).get();
        assertThat(likePost.getId(), is(likePost1.getId()));
    }

    @Test
    public void shouldFindLikeWhichNotExist() throws Exception {
        giveLike(secondUser);
        boolean foundLike = likePostRepository.findByUserAndPost(firstUser,blogPost).isPresent();
        assertThat(foundLike, is(false));
    }
}

package io.github.helloworlde.graphql.config;

import io.github.helloworlde.graphql.model.Post;
import io.github.helloworlde.graphql.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HelloWood
 */
@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Post> posts = initPost();
        posts.forEach(post -> log.info("Post: {}", post));
    }

    private List<Post> initPost() {
        postRepository.deleteAll();

        return Stream.of("Post one", "Post two")
                .map(title -> {
                    Post post = Post.builder()
                            .title(title)
                            .content("Content of " + title)
                            .build();
                    return postRepository.save(post);
                })
                .collect(Collectors.toList());
    }
}

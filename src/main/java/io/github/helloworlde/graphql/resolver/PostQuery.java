package io.github.helloworlde.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import io.github.helloworlde.graphql.model.Post;
import io.github.helloworlde.graphql.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author HelloWood
 */
@Component
public class PostQuery implements GraphQLQueryResolver {

    @Autowired
    private PostRepository postRepository;

    public List<Post> posts() {
        return postRepository.findAll();
    }

    public Optional<Post> post(String id) {
        return postRepository.findById(id);
    }

}

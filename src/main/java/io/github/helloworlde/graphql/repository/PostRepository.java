package io.github.helloworlde.graphql.repository;

import io.github.helloworlde.graphql.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author HelloWood
 */
public interface PostRepository extends MongoRepository<Post, String> {
}

package io.github.helloworlde.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import io.github.helloworlde.graphql.model.Post;
import io.github.helloworlde.graphql.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Post modify resolver
 *
 * @author HelloWood
 */
@Component
public class PostMutation implements GraphQLMutationResolver {

    @Autowired
    private PostRepository postRepository;

    /**
     * Create Post
     *
     * @param post The create Post entity
     * @return The created Post entity
     */
    public Post createPost(Post post) {
        Post newPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return postRepository.save(newPost);
    }

    /**
     * Update Post
     *
     * @param id   The update post id
     * @param post The update post entity
     * @return Had updated post entity
     * @throws Exception Throw exception when the entity is not found by the given id
     */
    public Post updatePost(String id, Post post) throws Exception {
        Post currentPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post " + id + " Not Exist"));

        currentPost.setTitle(post.getTitle());
        currentPost.setContent(post.getContent());

        return postRepository.save(currentPost);
    }

    /**
     * Delete Post
     *
     * @param id The delete Post id
     * @return The deleted Post's id
     * @throws Exception Throw exception when the entity is not found by the given id
     */
    public String deletePost(String id) throws Exception {
        postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post " + id + " Not Exist"));

        postRepository.deleteById(id);
        return id;
    }
}

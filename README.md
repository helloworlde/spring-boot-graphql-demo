# GraphQL Spring Boot 使用 

> 使用 SpringBoot 和 GraphQL 创建一个最简单的增删改查接口应用，使用 MongoDB 存储数据

## 创建应用

- 添加依赖

```groovy
dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web')
    implementation('org.springframework.boot:spring-boot-starter-data-mongodb')

    compileOnly('org.projectlombok:lombok')
    testImplementation('org.springframework.boot:spring-boot-starter-test')
}
```

### 添加基础接口 

- 添加 Model

```java
@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private String id;

    private String title;

    private String content;

    @CreatedDate
    private Date createDate;
}
```

- 添加 Repository 

```java
public interface PostRepository extends MongoRepository<Post, String> {
}
```

- 添加配置
```properties
# MongoDB Config
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
#spring.data.mongodb.username=
#spring.data.mongodb.password=
spring.data.mongodb.database=graphql
```

- 添加数据初始化

```java
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
```

### 添加 GraphQL 配置 

- 添加 GraphQL 依赖

```groovy
    implementation('com.graphql-java:graphql-spring-boot-starter:5.0.2')
    // 提供 UI    
    implementation('com.graphql-java:graphiql-spring-boot-starter:5.0.2')
    // 用于 Resolver    
    implementation('com.graphql-java:graphql-java-tools:5.2.4')
```

- 添加接口定义脚本

```graphql
# io.github.helloworlde.graphql.model.Post 对应的Model
type Post {
    id: ID,
    title: String,
    content: String,
    createDate: String
}


# io.github.helloworlde.graphql.resolver.PostMutation.updatePost 的入参 post
input PostInput{
    title: String!,
    content: String!
}

# 查询 io.github.helloworlde.graphql.resolver.PostQuery
type Query{
    posts: [Post]
    post(id: ID!): Post
}

# 修改 io.github.helloworlde.graphql.resolver.PostMutation
type Mutation{
    createPost(post: PostInput): Post!
    updatePost(id: ID!, post: PostInput): Post!
    deletePost(id: ID!): String
}
```

#### 添加接口 Resolver

- 查询 Resolver

```java
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
```

- 修改 Resolver

```java
@Component
public class PostMutation implements GraphQLMutationResolver {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        Post newPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return postRepository.save(newPost);
    }

    public Post updatePost(String id, Post post) throws Exception {
        Post currentPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post " + id + " Not Exist"));

        currentPost.setTitle(post.getTitle());
        currentPost.setContent(post.getContent());

        return postRepository.save(currentPost);
    }

    public String deletePost(String id) throws Exception {
        postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post " + id + " Not Exist"));

        postRepository.deleteById(id);
        return id;
    }
}
```

## 测试 

- 启动应用 

- 访问 [http://localhost:8080/graphiql](http://localhost:8080/graphiql)

![GraphQL](/img/GraphQL.png)

#### 查询 

- 查询列表

```graphql
{
  posts {
    id
    title
    content
    createDate
  }
}
```

```json
{
  "data": {
    "posts": [
      {
        "id": "5c50245b7ed65eacb3372aba",
        "title": "Post one",
        "content": "Content of Post one",
        "createDate": "Tue Jan 29 18:00:59 CST 2019"
      },
      {
        "id": "5c50245b7ed65eacb3372abb",
        "title": "Post two",
        "content": "Content of Post two",
        "createDate": "Tue Jan 29 18:00:59 CST 2019"
      }
    ]
  }
}
```

- 查询指定 id

```graphql
{
  post(id: "5c50245b7ed65eacb3372aba") {
    id
    title
    content
    createDate
  }
}
```

```json
{
  "data": {
    "post": {
      "id": "5c50245b7ed65eacb3372aba",
      "title": "Post one",
      "content": "Content of Post one",
      "createDate": "Tue Jan 29 18:00:59 CST 2019"
    }
  }
}
```

#### 修改

- 新增 

```graphql
mutation {
  createPost(post: {title: "New Posts", content: "New Post Content"}) {
    id
    title
    content
    createDate
  }
}
```

```json
{
  "data": {
    "createPost": {
      "id": "5c5027197ed65eaf47a0854d",
      "title": "New Posts",
      "content": "New Post Content",
      "createDate": "Tue Jan 29 18:12:41 CST 2019"
    }
  }
}
```

- 修改 

```graphql
mutation {
  updatePost(id: "5c5027197ed65eaf47a0854d", post: {title: "Update Posts", content: "Update Post Content"}) {
    id
    title
    content
    createDate
  }
}
```

```json
{
  "data": {
    "updatePost": {
      "id": "5c5027197ed65eaf47a0854d",
      "title": "Update Posts",
      "content": "Update Post Content",
      "createDate": "Tue Jan 29 18:12:41 CST 2019"
    }
  }
}
```

- 删除

```graphql
mutation {
  deletePost(id: "5c5027197ed65eaf47a0854d")
}
```

```json
{
  "data": {
    "deletePost": "5c5027197ed65eaf47a0854d"
  }
}
```
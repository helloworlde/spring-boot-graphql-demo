package io.github.helloworlde.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author HelloWood
 */
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

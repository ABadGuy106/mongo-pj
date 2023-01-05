package net.abafguy.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Document(collection = "comment") //可以省略，省略默认使用类名小写映射
//@CompoundIndex(def = "{'articleId':1,'userId':-1}") //创建复合索引,最好使用命令行创建
public class Comment implements Serializable {

    @Id
    private String id;


    @Field("content")//该属性对应mongodb的字段名称，如果一致可以不写
    private String content; //评论内容

    private Date publishTime; //发布日期

//    @Indexed //添加单个字段的索引
    private String userId; //发布人id

    private String nickName;//昵称

    private LocalDateTime createDateTime; //评论日期时间

    private Integer likeNum;//点赞数量

    private Integer replyNum;//回复数量

    private String status; //状态

    private String parentId; //上级id

//    @Indexed
    private String articleId; //文章id

    @Override
    public String toString() {
        return "{\"Comment\":{"
                + "\"id\":\""
                + id + '\"'
                + ",\"content\":\""
                + content + '\"'
                + ",\"publishTime\":\""
                + publishTime + '\"'
                + ",\"userId\":\""
                + userId + '\"'
                + ",\"nickName\":\""
                + nickName + '\"'
                + ",\"createDateTime\":"
                + createDateTime
                + ",\"likeNum\":"
                + likeNum
                + ",\"replyNum\":"
                + replyNum
                + ",\"status\":\""
                + status + '\"'
                + ",\"parentId\":\""
                + parentId + '\"'
                + ",\"articleId\":\""
                + articleId + '\"'
                + "}}";

    }
}

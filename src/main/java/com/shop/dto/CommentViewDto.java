package com.shop.dto;

import com.shop.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CommentViewDto {
    private Long id;
    private String memberName;
    private String content;
    private Long parentId;
    private String upTime;

    private static ModelMapper modelMapper = new ModelMapper();

    public Comment createComment() {
        return modelMapper.map(this, Comment.class);
    }

    public static CommentViewDto of(Comment comment) {
        CommentViewDto commentViewDto = new CommentViewDto();
        commentViewDto.setUpTime(comment.getRegTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        commentViewDto.setMemberName(comment.getMember().getName());
        if (comment.getParent() != null) {
            commentViewDto.setParentId(comment.getParent().getId());
        }
        return modelMapper.map(comment, CommentViewDto.class);
    }
}

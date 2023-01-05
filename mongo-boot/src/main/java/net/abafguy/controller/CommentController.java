package net.abafguy.controller;

import net.abafguy.po.Comment;
import net.abafguy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/save")
    public boolean save(@RequestBody Comment comment){
        comment.setCreateDateTime(LocalDateTime.now());
        comment.setPublishTime(new Date());
        commentService.save(comment);
        return true;
    }

    @GetMapping("/{id}")
    public Comment getById(@PathVariable("id") String id){
        return commentService.getById(id);
    }

    @GetMapping("/list")
    public List<Comment> list(){
        return commentService.list();
    }

}

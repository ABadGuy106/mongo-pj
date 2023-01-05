package net.abafguy.service;

import lombok.extern.slf4j.Slf4j;
import net.abafguy.dao.CommentRepository;
import net.abafguy.po.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;


    public void save(Comment comment){
        commentRepository.save(comment);
    }

    public Comment getById(String id) {
        return commentRepository.findById(id).get();
    }

    public List<Comment> list() {
        return commentRepository.findAll();
    }
}

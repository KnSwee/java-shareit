package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(Long itemId);

    @Query("select c " +
            "from Comment c " +
            "right join fetch c.item i " +
            "join fetch i.owner o " +
            "where o.id = :ownerId")
    List<Comment> getCommentsByOwnerId(Long ownerId);
}

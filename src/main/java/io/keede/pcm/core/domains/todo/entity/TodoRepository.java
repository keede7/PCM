package io.keede.pcm.core.domains.todo.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByUserIdOrderByIdAsc(Long userId);

    List<Todo> findAllByUserIdAndTitleContainingOrderByIdAsc(
            final Long userId,
            final String keyword
    );

    Optional<Todo> findByIdAndUserId(
            final Long id,
            final Long userId
    );

    /**
     * 물리적 FK가 없어 DB 차원의 cascade가 동작하지 않으므로, 회원 탈퇴 시 직접 삭제한다.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Todo t where t.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}

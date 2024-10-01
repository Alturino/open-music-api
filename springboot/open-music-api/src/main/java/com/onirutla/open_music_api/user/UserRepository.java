package com.onirutla.open_music_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>, UserDetailsService {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByRefreshToken(String refreshToken);

    @Query("""
            select u
            from users as u
                     inner join playlists as p on u.id = p.ownerId
                     inner join collaborations as c on p.id = c.playlistId
            where p.id = :playlistId and (c.collaboratorId = :userId or c.ownerId = :userId)
            """
    )
    Optional<UserEntity> isOwnerOrCollaboratorPlaylist(String userId, String playlistId);

    @Query("""
            select u
            from users as u
                     inner join playlists as p on u.id = p.ownerId
                     inner join collaborations as c on p.ownerId = c.ownerId
            where c.ownerId = :userId and c.playlistId = :playlistId
            """
    )
    Optional<UserEntity> isOwnerPlaylist(String userId, String playlistId);

    @Override
    default UserDetails loadUserByUsername(String username) {
        return findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username=%s not found".formatted(username)));
    }
}

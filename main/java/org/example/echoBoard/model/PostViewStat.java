package org.example.echoBoard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    private Integer viewCount = 0;

    public PostViewStat(Post post) {
        this.post = post;
        this.viewCount = 0;
    }

    public void increase(){
        this.viewCount+=1;
    }

}

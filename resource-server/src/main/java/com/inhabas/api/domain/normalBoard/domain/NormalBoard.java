package com.inhabas.api.domain.normalBoard.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inhabas.api.domain.board.domain.BaseBoard;
import com.inhabas.api.domain.board.domain.valueObject.Content;
import com.inhabas.api.domain.board.domain.valueObject.Title;
import com.inhabas.api.domain.comment.domain.Comment;
import com.inhabas.api.domain.menu.domain.Menu;

@Getter
@Entity
@Table(name = "NORMAL_BOARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorValue("NORMAL")
public class NormalBoard extends BaseBoard {

  @Embedded private Content content;

  @Column private Boolean isPinned = false;

  @Column(columnDefinition = "DATETIME(0)")
  private LocalDateTime datePinExpiration;

  @OneToMany(mappedBy = "parentBoard", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  /* constructor */

  public NormalBoard(
      String title, Menu menu, String content, Boolean isPinned, LocalDateTime datePinExpiration) {
    super(title, menu);
    this.content = new Content(content);
    this.isPinned = isPinned;
    this.datePinExpiration = datePinExpiration;
  }

  /* getter */

  public String getContent() {
    return content.getValue();
  }

  /* relation method */

  public void updateText(String title, String content) {
    this.title = new Title(title);
    this.content = new Content(content);
  }

  public void updatePinned(Boolean isPinned, LocalDateTime datePinExpiration) {
    this.isPinned = isPinned;
    this.datePinExpiration = datePinExpiration;
  }
}

package com.inhabas.api.domain.board.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inhabas.api.domain.board.domain.valueObject.Content;
import com.inhabas.api.domain.board.domain.valueObject.Title;
import com.inhabas.api.domain.menu.domain.Menu;

@Entity
@Table(name = "ALBUM_BOARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorValue("ALBUM")
public class AlbumBoard extends BaseBoard {

  @Column private Content content;

  @Builder
  public AlbumBoard(String title, Menu menu, String content) {
    super(title, menu);
    this.content = new Content(content);
  }

  public String getContent() {
    return content.getValue();
  }

  public void updateText(String title, String content) {
    this.title = new Title(title);
    this.content = new Content(content);
  }
}

package com.inhabas.api.domain.scholarship.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inhabas.api.domain.board.domain.BaseBoard;
import com.inhabas.api.domain.board.domain.valueObject.Content;
import com.inhabas.api.domain.board.domain.valueObject.Title;
import com.inhabas.api.domain.menu.domain.Menu;

@Entity
@Table(name = "SCHOLARSHIP_BOARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorValue("SCHOLARSHIP")
public class Scholarship extends BaseBoard {

  @Embedded private Content content;

  @Column(name = "DATE_HISTORY", nullable = false, columnDefinition = "DATETIME(0)")
  private LocalDateTime dateHistory;

  /* constructor */

  public Scholarship(String title, Menu menu, String content, LocalDateTime dateHistory) {
    super(title, menu);
    this.content = new Content(content);
    this.dateHistory = dateHistory;
  }

  /* getter */

  public String getContent() {
    return content.getValue();
  }

  public void updateText(String title, String content, LocalDateTime dateHistory) {
    this.title = new Title(title);
    this.content = new Content(content);
    this.dateHistory = dateHistory;
  }
}

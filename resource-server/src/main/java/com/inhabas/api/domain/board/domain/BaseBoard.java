package com.inhabas.api.domain.board.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inhabas.api.auth.domain.error.businessException.InvalidInputException;
import com.inhabas.api.auth.domain.oauth2.member.domain.entity.Member;
import com.inhabas.api.domain.BaseEntity;
import com.inhabas.api.domain.board.domain.valueObject.Title;
import com.inhabas.api.domain.board.exception.WriterUnmodifiableException;
import com.inhabas.api.domain.comment.domain.Comment;
import com.inhabas.api.domain.file.domain.BoardFile;
import com.inhabas.api.domain.menu.domain.Menu;

@Getter
@Entity(name = "BASE_BOARD")
@DiscriminatorColumn(name = "TYPE", length = 50)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseBoard extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Embedded protected Title title;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_BOARD_OF_USER_ID"))
  protected Member writer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "MENU_ID", foreignKey = @ForeignKey(name = "FK_BOARD_OF_MENU_ID"))
  protected Menu menu;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  protected List<BoardFile> files = new ArrayList<>();

  @OneToMany(mappedBy = "parentBoard", cascade = CascadeType.ALL, orphanRemoval = true)
  protected List<Comment> comments = new ArrayList<>();

  public <T extends BaseBoard> T writtenBy(Member writer, Class<T> boardClass) {

    if (this.writer == null) {
      if (!BaseBoard.class.isAssignableFrom(boardClass)) {
        throw new InvalidInputException();
      }
      this.writer = writer;
      return boardClass.cast(this);
    } else {
      throw new WriterUnmodifiableException();
    }
  }

  public boolean isWrittenBy(Member writer) {
    return this.writer.equals(writer);
  }

  public String getTitle() {
    return title.getValue();
  }

  public List<BoardFile> getFiles() {
    return Collections.unmodifiableList(files);
  }

  public BaseBoard(String title, Menu menu) {
    this.title = new Title(title);
    this.menu = menu;
  }

  private void addFile(BoardFile file) {
    if (this.files == null) {
      this.files = new ArrayList<>();
    }

    this.files.add(file);
  }

  public void updateFiles(List<BoardFile> files) {

    if (this.files != null) {
      this.files.clear();
    } else {
      this.files = new ArrayList<>();
    }

    for (BoardFile file : files) {
      addFile(file);
    }
  }

  public void addComment(Comment newComment) {
    comments.add(newComment);
  }
}

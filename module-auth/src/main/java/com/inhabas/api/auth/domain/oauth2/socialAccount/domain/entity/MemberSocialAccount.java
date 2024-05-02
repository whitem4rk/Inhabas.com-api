package com.inhabas.api.auth.domain.oauth2.socialAccount.domain.entity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inhabas.api.auth.domain.oauth2.OAuth2Provider;
import com.inhabas.api.auth.domain.oauth2.member.domain.entity.Member;
import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.Email;
import com.inhabas.api.auth.domain.oauth2.socialAccount.domain.valueObject.UID;

@Getter
@Entity
@Table(
    name = "USER_SOCIALACCOUNT",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "user_socialaccount_uid_provider_uindex",
          columnNames = {"PROVIDER", "UID"})
    }) //
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSocialAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "USER_ID")
  private Member member;

  @Embedded private Email email;

  @Embedded private UID uid;

  @Column(name = "PROVIDER")
  @Enumerated(EnumType.STRING)
  private OAuth2Provider provider;

  public String getUid() {
    return uid.getValue();
  }

  public String getEmail() {
    return email.getValue();
  }

  public MemberSocialAccount(Member member, String email, String uid, OAuth2Provider provider) {
    this.member = member;
    this.email = new Email(email);
    this.uid = new UID(uid);
    this.provider = provider;
  }

  public void SetUID(UID uid) {
    this.uid = uid;
  }
}

package com.inhabas.api.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class IbasInformation {
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    private LocalDateTime joined;

    @Column(name = "USER_INTRO")
    private String introduce;

    @Column(name = "USER_APPLY_PUBLISH")
    private Integer applyPublish;

    public IbasInformation(Role role, String introduce, Integer applyPublish) {
        this.role = role;
        this.introduce = introduce;
        this.applyPublish = applyPublish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IbasInformation)) return false;
        IbasInformation that = (IbasInformation) o;
        return getRole() == that.getRole()
                && getJoined().equals(that.getJoined())
                && getIntroduce().equals(that.getIntroduce())
                && getApplyPublish().equals(that.getApplyPublish());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getRole(),
                getJoined(),
                getIntroduce(),
                getApplyPublish());
    }

}
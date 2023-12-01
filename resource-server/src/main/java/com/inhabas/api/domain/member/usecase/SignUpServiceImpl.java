package com.inhabas.api.domain.member.usecase;

import com.inhabas.api.auth.domain.oauth2.majorInfo.dto.MajorInfoDto;
import com.inhabas.api.auth.domain.oauth2.majorInfo.usecase.MajorInfoService;
import com.inhabas.api.auth.domain.oauth2.member.domain.entity.Member;
import com.inhabas.api.auth.domain.oauth2.member.domain.exception.MemberNotFoundException;
import com.inhabas.api.auth.domain.oauth2.member.domain.service.MemberDuplicationChecker;
import com.inhabas.api.auth.domain.oauth2.member.domain.service.MemberService;
import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.MemberType;
import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.Role;
import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.SchoolInformation;
import com.inhabas.api.auth.domain.oauth2.member.repository.MemberRepository;
import com.inhabas.api.auth.domain.oauth2.member.security.socialAccount.MemberSocialAccount;
import com.inhabas.api.auth.domain.oauth2.member.security.socialAccount.MemberSocialAccountRepository;
import com.inhabas.api.domain.member.domain.exception.NotWriteAnswersException;
import com.inhabas.api.domain.member.domain.exception.NotWriteProfileException;
import com.inhabas.api.domain.member.dto.AnswerDto;
import com.inhabas.api.domain.member.dto.SignUpDto;
import com.inhabas.api.domain.questionaire.dto.QuestionnaireDto;
import com.inhabas.api.domain.questionaire.usecase.QuestionnaireService;
import com.inhabas.api.domain.signUpSchedule.domain.SignUpScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.MemberType.UNDERGRADUATE;
import static com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.Role.ANONYMOUS;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final AnswerService answerService;
    private final MemberService memberService;
    private final MajorInfoService majorInfoService;
    private final QuestionnaireService questionnaireService;
    private final SignUpScheduler signUpScheduler;
    private final MemberDuplicationChecker memberDuplicationChecker;
    private final MemberSocialAccountRepository memberSocialAccountRepository;

    private static final MemberType DEFAULT_MEMBER_TYPE = UNDERGRADUATE;
    private static final Role DEFAULT_ROLE_BEFORE_FINISH_SIGNUP = ANONYMOUS;


    @Override
    @Transactional
    public void saveSignUpForm(SignUpDto signUpForm, Long memberId) {

        Integer generation = signUpScheduler.getSchedule().getGeneration();
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        member.setName(signUpForm.getName());
        member.setPhone(signUpForm.getPhoneNumber());
        member.setStudentId(signUpForm.getStudentId());

        if (signUpForm.getGrade() != null) {
            member.setSchoolInformation(new SchoolInformation(signUpForm.getMajor(), signUpForm.getGrade(),
                    generation, signUpForm.getMemberType()));
        } else {
            // 교수
            member.setSchoolInformation(new SchoolInformation(signUpForm.getMajor(),
                    generation, signUpForm.getMemberType()));
        }

    }

    @Override
    public void completeSignUp(List<AnswerDto> answerDtoList, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        if (notYetWroteProfile(member)) {
            throw new NotWriteProfileException();
        }

        if (isContentNullInAnyDto(answerDtoList)) {
            throw new NotWriteAnswersException();
        }

        saveAnswers(answerDtoList, memberId);

        memberSocialAccountRepository.save(new MemberSocialAccount(member, member.getEmail(),
                member.getUid().getValue(), member.getProvider()));
        memberService.finishSignUp(member);
    }

    public boolean isContentNullInAnyDto(List<AnswerDto> answerDtoList) {
        for (AnswerDto dto : answerDtoList) {
            if (dto.getContent() == null) {
                return true;
            }
        }
        return false;
    }

    private boolean notYetWroteProfile(Member member) {

        if (member.getSchoolInformation().getGrade() == null || member.getSchoolInformation().getMemberType() == null
                || member.getSchoolInformation().getMajor() == null || member.getStudentId() == null
                || member.getPhone() == null) {
            return true;
        } else
            return false;

    }

    @Override
    public List<QuestionnaireDto> getQuestionnaire() {
        return questionnaireService.getQuestionnaire();
    }

    @Override
    public void saveAnswers(List<AnswerDto> answerDtoList, Long memberId) {
        answerService.saveAnswers(answerDtoList, memberId);
    }

    @Override
    public List<AnswerDto> getAnswers(Long memberId) {
        return answerService.getAnswers(memberId);
    }

    @Override
    public List<MajorInfoDto> getMajorInfo() {
        return majorInfoService.getAllMajorInfo();
    }

    @Override
    @Transactional(readOnly = true)
    public SignUpDto loadSignUpForm(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        return SignUpDto.builder()
                .studentId(member.getStudentId())
                .phoneNumber(member.getPhone())
                .name(member.getName())
                .major(member.getSchoolInformation() == null ? null : member.getSchoolInformation().getMajor())
                .memberType(member.getSchoolInformation() == null ? null : member.getSchoolInformation().getMemberType())
                .grade(member.getSchoolInformation() == null ? null : member.getSchoolInformation().getGrade())
                .build();

    }

}

package com.inhabas.api.domain.club.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.inhabas.api.auth.domain.oauth2.member.domain.entity.Member;
import com.inhabas.api.auth.domain.oauth2.member.repository.MemberRepository;
import com.inhabas.api.domain.board.domain.AlbumBoard;
import com.inhabas.api.domain.board.usecase.BoardSecurityChecker;
import com.inhabas.api.domain.club.dto.ClubActivityDetailDto;
import com.inhabas.api.domain.club.dto.ClubActivityDto;
import com.inhabas.api.domain.club.dto.SaveClubActivityDto;
import com.inhabas.api.domain.club.repository.ClubActivityRepository;
import com.inhabas.api.domain.file.repository.BoardFileRepository;
import com.inhabas.api.domain.member.domain.entity.MemberTest;
import com.inhabas.api.domain.menu.domain.Menu;
import com.inhabas.api.domain.menu.domain.MenuGroup;
import com.inhabas.api.domain.menu.domain.valueObject.MenuType;
import com.inhabas.api.domain.menu.repository.MenuRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class ClubActivityServiceImplTest {

  @InjectMocks private ClubActivityServiceImpl clubActivityService;
  @Mock private ClubActivityRepository clubActivityRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private BoardFileRepository boardFileRepository;
  @Mock private BoardSecurityChecker boardSecurityChecker;
  @Mock private MenuRepository menuRepository;

  @DisplayName("동아리 활동 조회 성공")
  @Transactional(readOnly = true)
  @Test
  public void getClubActivitiesTest_Success() {
    // given
    Member member = MemberTest.chiefMember();
    ClubActivityDto dto =
        ClubActivityDto.builder()
            .title("title")
            .writerName(member.getName())
            .dateCreated(LocalDateTime.now())
            .dateUpdated(LocalDateTime.now())
            .build();

    given(clubActivityRepository.findAllAndSearch(any())).willReturn(List.of(dto));

    // when
    List<ClubActivityDto> clubActivityDtoList = clubActivityService.getClubActivities("");

    // then
    assertThat(clubActivityDtoList).hasSize(1);
    assertThat(clubActivityDtoList.get(0).getTitle()).isEqualTo(dto.getTitle());
  }

  @DisplayName("동아리 활동 생성 성공")
  @Test
  public void writeClubActivityTest_Success() {
    // given
    Long memberId = 1L;
    Member member = MemberTest.chiefMember(); // 필요한 속성으로 Member 객체 초기화
    Menu menu = new Menu(mock(MenuGroup.class), 1, MenuType.ALBUM, "동아리 활동", "동아리 활동");
    SaveClubActivityDto saveClubActivityDto = new SaveClubActivityDto("title", "content", null);

    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
    given(menuRepository.findByName_Value(any())).willReturn(Optional.of(menu));
    given(clubActivityRepository.save(any(AlbumBoard.class)))
        .willAnswer(
            invocation -> {
              AlbumBoard savedClubActivity = invocation.getArgument(0);
              ReflectionTestUtils.setField(savedClubActivity, "id", 1L);
              return savedClubActivity;
            });

    // when
    Long resultId = clubActivityService.writeClubActivity(memberId, saveClubActivityDto);

    // then
    assertThat(resultId).isEqualTo(1L);
  }

  @DisplayName("동아리 활동 단일 조회 성공")
  @Test
  public void getClubActivityTest_Success() {
    // given
    AlbumBoard clubActivity =
        AlbumBoard.builder()
            .title("title")
            .content("content")
            .menu(mock(Menu.class))
            .build()
            .writtenBy(MemberTest.chiefMember(), AlbumBoard.class);

    given(clubActivityRepository.findById(any())).willReturn(Optional.of(clubActivity));

    // when
    ClubActivityDetailDto clubActivityDetailDto = clubActivityService.getClubActivity(1L);

    // then
    assertThat(clubActivityDetailDto)
        .as("Title and content of clubActivityDetailDto are equal to clubActivity")
        .extracting("title", "content")
        .containsExactly(clubActivity.getTitle(), clubActivity.getContent());
  }

  @DisplayName("동아리 활동 수정 성공")
  @Test
  public void updateClubActivityTest_Success() {
    // given
    Member member = MemberTest.chiefMember();
    AlbumBoard clubActivity =
        AlbumBoard.builder()
            .title("title")
            .content("content")
            .menu(mock(Menu.class))
            .build()
            .writtenBy(member, AlbumBoard.class);
    //    ReflectionTestUtils.setField(clubActivity, "id", 1L);
    SaveClubActivityDto saveClubActivityDto = new SaveClubActivityDto("title", "content", null);
    given(boardFileRepository.getAllByIdInAndUploader(anyList(), any()))
        .willReturn(new ArrayList<>());
    given(clubActivityRepository.findById(any())).willReturn(Optional.of(clubActivity));
    given(memberRepository.findById(any())).willReturn(Optional.of(member));

    // when
    clubActivityService.updateClubActivity(1L, saveClubActivityDto, 1L);

    // then
    then(boardFileRepository).should(times(1)).getAllByIdInAndUploader(anyList(), any());
    then(clubActivityRepository).should(times(1)).findById(any());
    then(memberRepository).should(times(1)).findById(any());
  }

  @DisplayName("동아리 활동 삭제 성공")
  @Test
  public void deleteClubActivityTest_Success() {
    // given
    Long boardId = 1L;
    doNothing().when(clubActivityRepository).deleteById(boardId);

    // when
    clubActivityService.deleteClubActivity(boardId);

    // then
    then(clubActivityRepository).should(times(1)).deleteById(boardId);
  }
}

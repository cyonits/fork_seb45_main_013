import styled from 'styled-components';
import { useState } from 'react';

import Button from '@mui/material/Button';

import Reviews from '@components/Reviews';
import PossibleReservationTime from '@components/PossibleReservationTime';

const PetsitterDetailsItem = [
  // 추후에 UseEffect로 데이터 받아올 데이터
  {
    id: 1,
    name: '리나',
    describe:
      '안녕하세요! 리나입니다. 펫시터를 시작한지 3년이 되었어요. 동물병원에서 근무하면서 반려견 케어하는 일을 도맡아서 진행하여 응급처치 해야할 상황에 잘 대처 할 수 있습니다 믿고 맡겨주시는 만큼 보답드리겠습니다. 잘 부탁드리고 편하게 문의 주세요! 감사합니다:)',
    rating: `5.0`,
    review: `1,630`,
    profileImg: '/imgs/PetsitterViewDetailsImg.svg',
    ratingImg: '/imgs/Star.svg',
    reviewImg: '/imgs/ReviewIcon.svg',
    location: '서울시 강남구',
  },
];

const NavItem = [
  {
    text: '가능한 예약 시간',
    link: '/possibleReservationTime',
  },
  {
    text: '이용 후기',
    link: '/reviews',
  },
];

const careerone = () => (
  <>
    <span>대표 경력&nbsp;&nbsp;&nbsp;&nbsp;</span>
    방문훈련 전문, 동물병원근무
  </>
);

const careertwo = () => (
  <>
    &nbsp;&nbsp;<span>전문 분야&nbsp;&nbsp;&nbsp;&nbsp;</span>
    공격성 교육 전문, 대형견 전문
  </>
);

const PetsitterViewDetails = () => {
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [activeTab, setActiveTab] = useState(NavItem[0].link);

  const handleBookmarkClick = () => {
    setIsBookmarked(!isBookmarked);
  };

  return (
    <>
      {PetsitterDetailsItem.map((item) => (
        <MainContainer key={item.id}>
          <ImgContainer>
            <img src={item.profileImg} alt="펫시터 사진" />
          </ImgContainer>
          <CareablePetContainer>
            <CareablePet>
              <img src="/icons/DogIcon.svg" alt="dogIcon" />
              <img src="/icons/CatIcon.svg" alt="catIcon" />
            </CareablePet>
          </CareablePetContainer>
          <PetsitterTextContainer>
            <LogoImg src="/imgs/Logo.svg" alt="Logo" />
            <PetsitterName>{item.name}</PetsitterName>
          </PetsitterTextContainer>
          <Introbox>
            <PetsitterIntroText>{item.describe}</PetsitterIntroText>
          </Introbox>
          <CareerContainer>
            <CareerText>{careerone()}</CareerText>
            <CareerText>{careertwo()}</CareerText>
          </CareerContainer>
          <BookmarkContainer>
            <RatingImg src={item.ratingImg} alt="ratingImg" />
            {item.rating}
            <MiddleLineImg src="/imgs/MiddleLine.svg" alt="middleLine" />
            <StyledButton variant="text" onClick={handleBookmarkClick}>
              <BookmarkIcon
                src={isBookmarked ? '/imgs/BeforeBookmark.svg' : '/icons/Bookmark.svg'}
                alt="bookmarkIcon"
              />
              찜하기
            </StyledButton>
          </BookmarkContainer>

          <ViewDetailsContainer>
            <TabButtonsContainer>
              {NavItem.map((nav) => (
                <NavBarButton key={nav.text} isActive={activeTab === nav.link} onClick={() => setActiveTab(nav.link)}>
                  {nav.text}
                </NavBarButton>
              ))}
            </TabButtonsContainer>
            <TabContentContainer>
              {activeTab === '/possibleReservationTime' && <PossibleReservationTime />}
              {activeTab === '/reviews' && <Reviews />}
            </TabContentContainer>
          </ViewDetailsContainer>
          <CustomLinkBtn>예약 하기</CustomLinkBtn>
        </MainContainer>
      ))}
    </>
  );
};

export default PetsitterViewDetails;

const MainContainer = styled.div`
  display: flex;
  flex-direction: column;
  position: relative;
  width: 100%;
  background-color: #fefdff;
`;

const ImgContainer = styled.div`
  position: absolute;
  width: 100%;
  top: -64px;
  border-radius: 0px 0px 110px 0px;
  height: 280px;
  overflow: hidden;
  z-index: 1;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

const CareablePetContainer = styled.div`
  top: -64px;
  right: 0;
  padding: 20px;
  position: absolute;
`;

const PetsitterTextContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 3;
  margin-top: 144px;
`;

const LogoImg = styled.img`
  width: 100%;
  height: 21px;
`;

const CareablePet = styled.div`
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  gap: 4px;
`;

const PetsitterName = styled.div`
  color: ${(props) => props.theme.colors.white};
  font-size: ${(props) => props.theme.fontSize.s20h30};
  font-weight: ${(props) => props.theme.fontWeights.extrabold};
`;

const Introbox = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const PetsitterIntroText = styled.div`
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  overflow-y: auto;
  max-width: 70%;
  max-height: 200px;
  margin-top: 40px;
  color: ${(props) => props.theme.textColors.gray50};
  font-size: ${(props) => props.theme.fontSize.s14h21};
  font-weight: ${(props) => props.theme.fontWeights.normal};

  /* Hide scrollbar for Chrome, Safari and Opera */
  ::-webkit-scrollbar {
    display: none;
  }
  /* Hide scrollbar for Firefox */
  scrollbar-width: none;
`;

const CareerContainer = styled.div`
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  text-align: center;
`;

const CareerText = styled.div`
  font-size: ${(props) => props.theme.fontSize.s14h21};

  span {
    font-size: ${(props) => props.theme.fontSize.s18h27};
    font-weight: ${(props) => props.theme.fontWeights.extrabold};
    color: ${(props) => props.theme.colors.mainBlue};
  }
`;

const BookmarkContainer = styled.div`
  margin-top: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 14px 12px 0 12px;
  padding: 8px 16px;
  background-color: ${(props) => props.theme.colors.white};
  box-shadow: ${(props) => props.theme.shadow.dp01};
  border-radius: 8px;
  color: ${(props) => props.theme.textColors.gray00};
  font-size: ${(props) => props.theme.fontSize.s18h27};
  font-weight: ${(props) => props.theme.fontWeights.extrabold};
`;

const RatingImg = styled.img`
  width: 18px;
  height: 18px;
  margin: 0 32px 0 0;
`;

const MiddleLineImg = styled.img`
  margin: 0 18px 0 34px;
`;

const BookmarkIcon = styled.img`
  width: 18px;
  height: 18px;
  margin: 0 18px 0 8px;
`;

const StyledButton = styled(Button)`
  display: flex;
  justify-content: space-around;
  gap: 12px;
`;

const ViewDetailsContainer = styled.div`
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  background-color: ${(props) => props.theme.colors.white};
  margin: 16px 12px 12px 12px;
  padding-top: 16px; !important;
  height: 320px;
  box-shadow: ${(props) => props.theme.shadow.dp01};
`;

const CustomLinkBtn = styled.button`
  border-radius: 8px;
  margin: 12px 12px 12px 12px;
  padding: 12px;
  color: white;
  text-align: center;
  margin-top: 16px;
  background-color: ${({ theme }) => theme.colors.mainBlue};
`;

const NavBarButton = styled.button<{ isActive: boolean }>`
  width: 100%;
  flex: 1;
  border: none;
  background-color: white;
  font-weight: ${(props) => (props.isActive ? props.theme.fontWeights.extrabold : props.theme.fontWeights.bold)};
  color: ${(props) => (props.isActive ? 'black' : props.theme.textColors.gray30)};
  border-bottom: ${(props) => (props.isActive ? `2px solid ${props.theme.colors.mainBlue}` : null)};
  padding-bottom: 36px;
  margin-bottom: ${(props) => (props.isActive ? '-2px' : '0px')};
  ${(props) => props.theme.fontSize.s14h21};
  height: 16%;
`;

const TabButtonsContainer = styled.div`
  display: flex;
  flex-direction: row;
`;

const TabContentContainer = styled.div`
  display: flex;
`;

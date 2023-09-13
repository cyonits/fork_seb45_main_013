package shop.petmily.domain.reservation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shop.petmily.domain.journal.mapper.JournalMapper;
import shop.petmily.domain.member.entity.Petsitter;
import shop.petmily.domain.reservation.dto.*;
import shop.petmily.domain.reservation.entity.Reservation;
import shop.petmily.domain.reservation.mapper.ReservationMapper;
import shop.petmily.domain.reservation.repository.ReservationQueryDsl;
import shop.petmily.domain.reservation.service.ReservationService;
import shop.petmily.domain.review.mapper.ReviewMapper;
import shop.petmily.global.argu.LoginMemberId;
import shop.petmily.global.dto.PageInfo;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationMapper mapper;
    private final ReservationService service;
    private final JournalMapper journalMapper;
    private final ReviewMapper reviewMapper;
    private final ReservationQueryDsl reservationQueryDsl;


    public ReservationController(ReservationMapper mapper, ReservationService service,
                                 JournalMapper journalMapper, ReviewMapper reviewMapper,
                                 ReservationQueryDsl reservationQueryDsl) {
        this.mapper = mapper;
        this.service = service;
        this.journalMapper = journalMapper;
        this.reviewMapper = reviewMapper;
        this.reservationQueryDsl = reservationQueryDsl;
    }

    //예약가능 펫시터 list보여주기
    @PostMapping("/petsitters")
    public ResponseEntity findPetsitter(@Valid  @RequestBody ReservationPostDto reservationPostDto,
                                        @LoginMemberId Long memberId) {

        reservationPostDto.setMemberId(memberId);
        Reservation reservation = mapper.reservationPostDtoToReservation(reservationPostDto);

        List<Petsitter> petsitters = service.findReservationPossiblePetsitter(reservation);

        List<PossiblePetsitterResponseDto> petsitterResponse =
                petsitters.stream()
                        .map(petsitter -> mapper.petsitterToReservationPossiblePetsitterReseponseDto(petsitter))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(petsitterResponse, HttpStatus.OK);
    }

    //예약정보 + 펫시터정보 등록하고 예약신청상태로 만들기
    @PostMapping
    public ResponseEntity createResrvation(@Valid @RequestBody ReservationPostDto reservationPostDto,
                                           @LoginMemberId Long memberId) {
        reservationPostDto.setMemberId(memberId);
        Reservation reservation = mapper.reservationPostDtoToReservation(reservationPostDto);
        Reservation createdReservation = service.createReservation(reservation);
        ReservationResponseDto response = mapper.reservationToReservationResponseDto(createdReservation);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 예약 1개 조회
    @GetMapping("/{reservation-id}")
    public ResponseEntity getReservation(@PathVariable("reservation-id") @Positive long reservationId) {
        Reservation reservation = service.findReservation(reservationId);
        ReservationResponseDto response = mapper.reservationToReservationResponseDto(reservation);

        response.setJournal(
                journalMapper.JournalToResponse(reservationQueryDsl.findJournalByReservation(reservation)));

        response.setReview(
                reviewMapper.reviewToResponse(reservationQueryDsl.findReviewByReservation(reservation)));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 예약 조회 (멤버)
    @GetMapping("/member")
    public ResponseEntity getReservationsForMember(@RequestParam("page") @Positive int page,
                                                   @RequestParam("size") @Positive int size,
                                                   @RequestParam(value = "condition", required = false) String condition,
                                                   @LoginMemberId Long memberId) {
        Page<Reservation> reservationPage = service.findMemberReservations(page, size, memberId, condition);
        PageInfo pageInfo = new PageInfo(page, size, (int) reservationPage.getTotalElements(), reservationPage.getTotalPages());

        List<Reservation> reservations = reservationPage.getContent();
        List<ReservationResponseDto> response =
                reservations.stream()
                        .map(reservation -> {
                            ReservationResponseDto reservationResponseDto =
                                    mapper.reservationToReservationResponseDto(reservation);

                            reservationResponseDto.setJournal(
                                    journalMapper.JournalToResponse(reservationQueryDsl.findJournalByReservation(reservation)));

                            reservationResponseDto.setReview(
                                    reviewMapper.reviewToResponse(reservationQueryDsl.findReviewByReservation(reservation)));

                                    return reservationResponseDto;
                                }).collect(Collectors.toList());

        return new ResponseEntity<>(new ReservationMultiResponseDto(response, pageInfo), HttpStatus.OK);
    }

    // 예약 조회 (펫시터)
    @GetMapping("/petsitter")
    public ResponseEntity getReservationsForPetSitter(@RequestParam("page") @Positive int page,
                                                      @RequestParam("size") @Positive int size,
                                                      @RequestParam(value = "condition", required = false) String condition,
                                                      @LoginMemberId Long memberId) {
        Page<Reservation> reservationPage = service.findPetsitterReservations(page, size, memberId, condition);
        PageInfo pageInfo = new PageInfo(page, size, (int) reservationPage.getTotalElements(), reservationPage.getTotalPages());

        List<Reservation> reservations = reservationPage.getContent();
        List<ReservationResponseDto> response =
                reservations.stream()
                        .map(reservation -> {
                            ReservationResponseDto reservationResponseDto =
                                    mapper.reservationToReservationResponseDto(reservation);

                            reservationResponseDto.setJournal(
                                    journalMapper.JournalToResponse(reservationQueryDsl.findJournalByReservation(reservation)));

                            reservationResponseDto.setReview(
                                    reviewMapper.reviewToResponse(reservationQueryDsl.findReviewByReservation(reservation)));

                            return reservationResponseDto;
                        }).collect(Collectors.toList());

        return new ResponseEntity<>(new ReservationMultiResponseDto(response, pageInfo), HttpStatus.OK);
    }

    //펫시터 오늘날짜 이후 취소아닌 예약일정 찾기
    @GetMapping("/schedule/{petsitter-id}")
    public ResponseEntity getPetsitterSchedule(@PathVariable("petsitter-id") @Positive long petsitterId){
        List<PetsitterScheduledResponseDto> response = service.getPetsitterSchedule(petsitterId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 예약 확정 (펫시터)
    @PatchMapping("/{reservation-id}/confirm")
    public HttpStatus confirmReservation(@PathVariable("reservation-id") @Positive long reservationId,
                                         @LoginMemberId Long memberId) {
        service.confirmReservationStatus(reservationId, memberId);

        return HttpStatus.OK;
    }

    // 예약 취소 (펫시터)
    @PatchMapping("/{reservation-id}/petsittercancel")
    public HttpStatus cancelReservationPetsitter(@PathVariable("reservation-id") @Positive long reservationId,
                                                 @LoginMemberId Long memberId) {
        service.cancelReservationPetsitter(reservationId, memberId);

        return HttpStatus.OK;
    }

    //예약 취소(멤버)
    @PatchMapping("/{reservation-id}/membercancel")
    public HttpStatus cancelReservationMember(@PathVariable("reservation-id") @Positive long reservationId,
                                              @LoginMemberId Long memberId) {
        service.cancelReservationMember(reservationId, memberId);

        return HttpStatus.OK;
    }
}
package shop.petmily.domain.pet.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.petmily.domain.member.entity.Member;
import shop.petmily.domain.member.service.MemberService;
import shop.petmily.domain.pet.entity.Pet;
import shop.petmily.domain.pet.repository.PetRepository;
import shop.petmily.global.AWS.service.S3UploadService;
import shop.petmily.global.exception.BusinessLogicException;
import shop.petmily.global.exception.ExceptionCode;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {
    private final PetRepository repository;
    private final S3UploadService uploadService;
    private final MemberService memberService;

    public PetService(PetRepository repository,
                      S3UploadService uploadService,
                      MemberService memberService){
        this.repository = repository;
        this.uploadService = uploadService;
        this.memberService = memberService;
    }

    public Pet createPet(Pet pet, MultipartFile file){
        if(file != null) pet.setPhoto(uploadService.saveFile(file));
        repository.save(pet);

        return repository.save(pet);
    }

    public Pet updatePet(Pet pet, MultipartFile file){
        Pet verifiedPet = verifiedPet(pet.getPetId());
        String beforeFileName = null;

        verifiedPetOwner(verifiedPet.getMember().getMemberId(), pet.getMember().getMemberId());

        Optional.ofNullable(pet.getAge())
                .ifPresent(age -> verifiedPet.setAge(age));
        Optional.ofNullable(pet.getWeight())
                .ifPresent(weight -> verifiedPet.setWeight(weight));
        Optional.ofNullable(pet.getName())
                .ifPresent(name -> verifiedPet.setName(name));
        Optional.ofNullable(pet.getBody())
                .ifPresent(body -> verifiedPet.setBody(body));
        Optional.ofNullable(pet.getNeutering())
                .ifPresent(neutering -> {
                    if (verifiedPet.getNeutering() == false && neutering) {
                        verifiedPet.setNeutering(neutering);
                    } else {
                        throw new BusinessLogicException(ExceptionCode.ALREADY_NEUTERING);
                    }
                });

        if(verifiedPet.getPhoto() != null) beforeFileName = verifiedPet.getPhoto();
        if(file != null) verifiedPet.setPhoto(uploadService.saveFile(file));

        Pet savedPet = repository.save(verifiedPet);

        if(beforeFileName != null) uploadService.deleteFile(beforeFileName);

        return savedPet;
    }

    public Pet photoDelete(Long petId, Long requestMemberId){
        Pet verifiedPet = verifiedPet(petId);
        verifiedPetOwner(verifiedPet.getMember().getMemberId(), requestMemberId);
        String beforeFileName = null;

        if(verifiedPet.getPhoto() != null) {
            beforeFileName = verifiedPet.getPhoto();
        } else {
            throw new BusinessLogicException(ExceptionCode.NO_PHOTO);
        }

        verifiedPet.setPhoto(null);
        Pet savedPet = repository.save(verifiedPet);

        uploadService.deleteFile(beforeFileName);

        return savedPet;
    }

    public Pet findPet(Long petId){
        Pet verifiedPet = verifiedPet(petId);
        return  verifiedPet;
    }

    public List<Pet> findPets(Long memberId){
        Member member = memberService.findMember(memberId);
        return  repository.findByMember(member);
    }

    public void deletePet(Long petId, Long requestMemberId){
        Pet verifiedPet = verifiedPet(petId);
        verifiedPetOwner(verifiedPet.getMember().getMemberId(), requestMemberId);
        repository.delete(verifiedPet);
    }

    private Pet verifiedPet(Long petId) {
        Optional<Pet> optionalPet = repository.findById(petId);
        Pet pet = optionalPet.orElseThrow(() -> new BusinessLogicException(ExceptionCode.PET_NOT_EXIST));
        return pet;
    }

    public void verifiedPetOwner(long originMemberId, Long requestMemberId) {
        if (originMemberId != requestMemberId) throw new BusinessLogicException(ExceptionCode.NOT_MY_PET);
    }
}

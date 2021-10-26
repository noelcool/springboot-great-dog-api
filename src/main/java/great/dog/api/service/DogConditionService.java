package great.dog.api.service;

import great.dog.api.domain.entity.Dog;
import great.dog.api.domain.entity.DogCondition;
import great.dog.api.domain.request.DogConditionRequest;
import great.dog.api.domain.response.DogConditionResponse;
import great.dog.api.domain.response.DogResponse;
import great.dog.api.repository.DogConditionRepository;
import great.dog.api.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DogConditionService {

    private final ModelMapper modelMapper;
    private final DogConditionRepository dogConditionRepository;
    private final DogRepository dogRepository;
    
    public List<DogConditionResponse> findAll() {
        List<DogCondition> dogConditions = dogConditionRepository.findAll();
        return dogConditions.stream().map(d -> modelMapper.map(d, DogConditionResponse.class)).collect(Collectors.toList());
    }

    public DogConditionResponse findById(Long id) {
        Optional<DogCondition> dogCondition = dogConditionRepository.findByIdAndDelYn(id, "N");
        return dogCondition.map(value -> modelMapper.map(value, DogConditionResponse.class)).orElse(null);
    }

    public List<DogConditionResponse> findByDogId(Long dogId) {
        List<DogCondition> dogConditions = dogConditionRepository.findByDogIdAndDelYn(dogId, "N");
        return dogConditions.stream().map(value -> modelMapper.map(value, DogConditionResponse.class)).collect(Collectors.toList());
    }

    @Transactional
    public int save(DogConditionRequest dto) {
        if (dto.getDogId() == null) return -1;
        Optional<Dog> oDog = dogRepository.findByIdAndDelYn(dto.getDogId(), "N");
        if (!oDog.isPresent()) return -1;
        DogCondition.DogConditionBuilder builder = DogCondition.builder().
                weight(dto.getWeight()).
                height(dto.getHeight()).
                dog(oDog.get());
        return dogConditionRepository.save(builder.build()) != null ? 1 : 0;
    }

    @Transactional
    public int update(Long id, DogConditionRequest dto) {
        Optional<DogCondition> dogCondition = dogConditionRepository.findByIdAndDelYn(id, "N");
        if(!dogCondition.isPresent()) return -1;
        dogCondition.ifPresent(d -> {
            if(dto.getWeight() != null) d.setWeight(dto.getWeight());
            if(dto.getHeight() != null) d.setHeight(dto.getHeight());
            dogConditionRepository.save(d);
        });
        return 1;
    }

}

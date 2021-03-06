package great.dog.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import great.dog.api.domain.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import great.dog.api.domain.entity.User;
import great.dog.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	public List<UserDto.Response> findAll() {
		List<User> users = userRepository.findAll();
		users.forEach(user -> {
			user.getDogs().forEach(dog -> {
				dog.getId();
			});
		});
		return users.stream().map(u -> modelMapper.map(u, UserDto.Response.class)).collect(Collectors.toList());
	}

	public UserDto.Response findById(Long id) {
		Optional<User> user = userRepository.findById(id);
/*
		user.ifPresent(u -> {
			u.getDogs().forEach(dog -> {
				dog.getDogConditions().forEach(dogCondition -> {
					dogCondition.getId();
				});
				dog.getDogDiseases().forEach(dogDisease -> {
					dogDisease.getId();
				});
				dog.getFeeding().forEach(feeding -> {
					feeding.getId();
				});
				dog.getHospital().forEach(hospital -> {
					hospital.getId();
				});
			});	
		});
*/
		return user.map(value -> modelMapper.map(value, UserDto.Response.class)).orElse(null);
	}

	public int save(UserDto.SaveRequest dto) {
		Optional<User> user = userRepository.findByName(dto.getName());
		if (user.isPresent()) return -2;
		User.UserBuilder userBuilder = User.builder().
				email(dto.getEmail()).
				name(dto.getName()).
				password(passwordEncoder.encode(dto.getPassword()));
		return userRepository.save(userBuilder.build()) != null ? 1 : 0; //great.dog.api.domain.entity.User@3a2749e0
	}

	@Transactional
	public int update(Long id, UserDto.UpdateRequest dto) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) return -1;
		user.ifPresent(u -> {
			u.setPassword(dto.getPassword());
			//u.setNickName(dto.getNickName());
			u.setDelYn(dto.getDelYn());
			userRepository.save(u);
		});
		return 1;
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public UserDto.SaveRequest join(UserDto.SaveRequest value) {
		//User user = save(value);
		//saveUserRole(user, RoleType.ROLE_VIEW);
		//return user.getSimple();
		return null;
	}

}

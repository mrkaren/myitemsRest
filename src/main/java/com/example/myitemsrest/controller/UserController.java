package com.example.myitemsrest.controller;

import com.example.myitemsrest.dto.*;
import com.example.myitemsrest.entity.Role;
import com.example.myitemsrest.entity.User;
import com.example.myitemsrest.repository.UserRepository;
import com.example.myitemsrest.security.CurrentUser;
import com.example.myitemsrest.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RestTemplate restTemplate;

    @Value("${myitems.cb.url}")
    private String cbUrl;

    @PostMapping("/users/auth")
    public ResponseEntity<UserLoginResponse> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest.getEmail() != null && !userLoginRequest.getEmail().equals("")) {
            log.info(userLoginRequest.getEmail() + " wants to get a token");
            Optional<User> byEmail = userRepository.findByEmail(userLoginRequest.getEmail());
            if (byEmail.isEmpty() || !passwordEncoder.matches(userLoginRequest.getPassword(), byEmail.get().getPassword())) {
                log.warn("passwords are not matching for user: " + userLoginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            log.info("user with email: {} get the token", userLoginRequest.getEmail());
            return ResponseEntity.ok(new UserLoginResponse(jwtTokenUtil.generateToken(byEmail.get().getEmail())));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/users/{id}")
    public UserResponseDto getUserById(@PathVariable("id") int id) {

        ResponseEntity<University[]> forEntity = restTemplate.getForEntity("http://universities.hipolabs.com/search?country=Armenia", University[].class);
        University[] universities = forEntity.getBody();
//        List<University> universityList = Arrays.asList(universities);
        for (University university : universities) {
            System.out.println(university);
        }
        return modelMapper.map(userRepository.getById(id), UserResponseDto.class);
    }

    @GetMapping("/users/")
    @CrossOrigin(origins = "http://localhost:8080")
    public List<UserResponseDto> getUsers(@AuthenticationPrincipal CurrentUser currentUser) {
        System.out.println(currentUser.getUser().getName());
        List<UserResponseDto> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            result.add(modelMapper.map(user, UserResponseDto.class));
        }
        return result;
    }

    @PostMapping("/users/")
    public @ResponseBody
    ResponseEntity<UserResponseDto> saveUser(@RequestBody SaveUserRequest saveUserRequest,
                             @RequestHeader(value = "platform", required = false) String platform) {
        User user = modelMapper.map(saveUserRequest, User.class);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(saveUserRequest.getPassword()));

        if(userRepository.findByEmail(saveUserRequest.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        String rubCurrencyUrl = cbUrl ;
        ResponseEntity<HashMap> rubCurrency = restTemplate.getForEntity(rubCurrencyUrl, HashMap.class);
        HashMap<String, String> currencyMap = rubCurrency.getBody();
        if (currencyMap != null) {
            String rub = currencyMap.get("RUB");
            System.out.println(rub);
        }

        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody SaveUserRequest saveUserRequest,
                                                      @PathVariable("id") int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User byId = user.get();
        if (saveUserRequest.getEmail() != null) {
            byId.setEmail(saveUserRequest.getEmail());
        }
        if (saveUserRequest.getName() != null) {
            byId.setName(saveUserRequest.getName());
        }
        if (saveUserRequest.getSurname() != null) {
            byId.setSurname(saveUserRequest.getSurname());
        }
        if (saveUserRequest.getPassword() != null) {
            byId.setPassword(saveUserRequest.getPassword());
        }
        userRepository.save(byId);
        UserResponseDto userResponseDto = modelMapper.map(byId, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteById(@PathVariable("id") int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

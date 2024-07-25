package com.springboot.backendprompren.service.impl;

import com.springboot.backendprompren.data.dto.response.ResponseCompetitionDto;
import com.springboot.backendprompren.data.dto.resquest.RequestCompetitionDto;
import com.springboot.backendprompren.data.entity.Competition;
import com.springboot.backendprompren.data.entity.User;
import com.springboot.backendprompren.data.repository.CompetitionRepository;
import com.springboot.backendprompren.data.repository.UserRepository;
import com.springboot.backendprompren.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseCompetitionDto createCompetition(RequestCompetitionDto requestDto) throws Exception {
        // Title 중복 확인
        Optional<Competition> existingCompetition = competitionRepository.findByTitle(requestDto.getTitle());
        if (existingCompetition.isPresent()) {
            throw new IllegalArgumentException("Title already exists.");
        }
        // 사용자 인증정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.getByAccount(username);
        Competition competition = new Competition();
        competition.setUser(user);
        competition.setTitle(requestDto.getTitle());
        competition.setContent(requestDto.getContent());
        competition.setImage(requestDto.getImage());
        competition.setCreatedAt(LocalDateTime.now());
        competition.setUpdatedAt(LocalDateTime.now());

        Competition savedCompetition = competitionRepository.save(competition);
        return new ResponseCompetitionDto(savedCompetition);
    }

    @Override
    public void deleteCompetition(Long com_id) {
        competitionRepository.deleteById(com_id);

    }

    @Override
    public List<ResponseCompetitionDto> getCompetitions() {
        List<Competition> competitions = competitionRepository.findAll();
        return competitions.stream()
                .map(ResponseCompetitionDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public long countCompetitions() {
        return competitionRepository.count();
    }
}

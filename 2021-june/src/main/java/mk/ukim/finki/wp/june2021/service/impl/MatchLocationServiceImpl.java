package mk.ukim.finki.wp.june2021.service.impl;

import mk.ukim.finki.wp.june2021.model.MatchLocation;
import mk.ukim.finki.wp.june2021.model.exceptions.InvalidMatchIdException;
import mk.ukim.finki.wp.june2021.repository.MatchLocationRepository;
import mk.ukim.finki.wp.june2021.service.MatchLocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchLocationServiceImpl implements MatchLocationService {

    private final MatchLocationRepository matchLocationRepository;

    public MatchLocationServiceImpl(MatchLocationRepository matchLocationRepository) {
        this.matchLocationRepository = matchLocationRepository;
    }

    @Override
    public MatchLocation findById(Long id) {
        return this.matchLocationRepository.findById(id)
                .orElseThrow(InvalidMatchIdException::new);
    }

    @Override
    public List<MatchLocation> listAll() {
        return this.matchLocationRepository.findAll();
    }

    @Override
    public MatchLocation create(String name) {
        MatchLocation matchLocation=new MatchLocation(name);
        return this.matchLocationRepository.save(matchLocation);
    }
}

package com.example.File.sharing.service;

import com.example.File.sharing.entity.LifeTime;
import com.example.File.sharing.repository.LifeTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LifeTimeService {
    public final LifeTimeRepository lifeTimeRepository;

    public LifeTime save(LifeTime LifeTime) {
        return lifeTimeRepository.save(LifeTime);
    }
}

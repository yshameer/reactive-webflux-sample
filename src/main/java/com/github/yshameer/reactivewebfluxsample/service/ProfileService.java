package com.github.yshameer.reactivewebfluxsample.service;

import com.github.yshameer.reactivewebfluxsample.entity.Profile;
import com.github.yshameer.reactivewebfluxsample.event.ProfileCreatedEvent;
import com.github.yshameer.reactivewebfluxsample.repository.ProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class ProfileService {

    private final ApplicationEventPublisher publisher;
    private final ProfileRepository profileRepository;

    ProfileService(ApplicationEventPublisher publisher, ProfileRepository profileRepository) {
        this.publisher = publisher;
        this.profileRepository = profileRepository;
    }

    public Flux<Profile> all() {
        return this.profileRepository.findAll();
    }

    public Mono<Profile> get(String id) {
        return this.profileRepository.findById(id);
    }

    public Mono<Profile> update(String id, String email) {
        return this.profileRepository
                .findById(id)
                .map(p -> Profile.builder().id(p.getId()).email(email).build())
                .flatMap(this.profileRepository::save);
    }

    public Mono<Profile> delete(String id) {
        return this.profileRepository
                .findById(id)
                .flatMap(p -> this.profileRepository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<Profile> create(String email) {
        return this.profileRepository
                .save(Profile.builder().id(null).email(email).build())
                .doOnSuccess(profile -> this.publisher.publishEvent(new ProfileCreatedEvent(profile)));
    }
}

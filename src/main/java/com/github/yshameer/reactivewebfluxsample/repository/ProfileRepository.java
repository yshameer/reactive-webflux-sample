package com.github.yshameer.reactivewebfluxsample.repository;


import com.github.yshameer.reactivewebfluxsample.entity.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
}

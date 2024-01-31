package com.ite5year.services;


import com.ite5year.models.SharedParam;
import com.ite5year.repositories.SharedParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SharedParametersServiceImpl implements SharedParametersService {
    SharedParametersRepository sharedParametersRepository;
    @Autowired
    private RedisTemplate<String, SharedParam> redisTemplate;
    private HashOperations<String, String, SharedParam> hashOperations;

    public SharedParametersServiceImpl() {
    }


    @Autowired
    public void setSharedParametersRepository(SharedParametersRepository sharedParametersRepository) {
        this.sharedParametersRepository = sharedParametersRepository;
    }

    private Map<String, SharedParam> mergeSharedParamsFromDBToRedis() {
        Map<String, SharedParam> dbParams = sharedParametersRepository.findAll().stream()
                .collect(Collectors.toMap(SharedParam::getFieldKey, Function.identity()));
        hashOperations.putAll("SharedParameters", dbParams);
        return dbParams;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, SharedParam> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = this.redisTemplate.opsForHash();
    }

    @Override
    @ResponseBody
    public SharedParam save(SharedParam sharedParam) {
        SharedParam savedSharedParam = sharedParametersRepository.save(sharedParam);
        hashOperations.put("SharedParameters", savedSharedParam.getFieldKey(), savedSharedParam);
        return savedSharedParam;
    }

    @Override
    public Map<String, SharedParam> findAll() {
        try {
            Map<String, SharedParam> hashedParams = hashOperations.entries("SharedParameters");
            if (hashedParams.size() > 0) return hashedParams;
            return mergeSharedParamsFromDBToRedis();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public SharedParam findByKey(String key) {
        SharedParam sharedParam = hashOperations.get("SharedParameters", key);
        if(sharedParam == null) {
            Optional<SharedParam> optionalSharedParam = sharedParametersRepository
                    .findAll()
                    .stream()
                    .filter(sp -> sp.getFieldKey().equals(key))
                    .findFirst();
            optionalSharedParam.ifPresent(sp -> hashOperations.put("SharedParameters", key, sp));
            return optionalSharedParam.get();
        }
        return sharedParam;
    }

    @Override
    public void update(SharedParam sharedParam) {
        save(sharedParam);
    }

    @Override
    public void delete(String key) {
        Optional<SharedParam> optionalSharedParam = sharedParametersRepository
                .findAll()
                .stream()
                .filter(sharedParam -> sharedParam.getFieldKey().equals(key))
                .findFirst();
        optionalSharedParam.ifPresent(sharedParam -> sharedParametersRepository.delete(sharedParam));
        hashOperations.delete("SharedParameters", key);
    }
}

package ru.nsu.fit.g20221.DIContainer.impl.testModel;

import ru.nsu.fit.g20221.DIContainer.annotation.Bean;
import ru.nsu.fit.g20221.DIContainer.annotation.Config;
import ru.nsu.fit.g20221.DIContainer.annotation.Name;

@Config
public class ConfigurationObject {
    @Bean
    public Pet pet(Human human, @Name(value = "houseJavaConfig") House house) {
        return new Pet(house, human);
    }
}

package com.wojcik;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;

public class CarService extends EntityService<Car>{

    protected CarService(RestHighLevelClient client, ObjectMapper objectMapper) {
        super(client, objectMapper);
    }

    @Override
    Class<Car> getEntityType() {
        return Car.class;
    }
}

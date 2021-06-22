package com.wojcik;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;

 class WorkstationService extends EntityService<Workstation> {

     protected WorkstationService(RestHighLevelClient client, ObjectMapper objectMapper) {
         super(client, objectMapper);
     }

     @Override
     Class<Workstation> getEntityType() {
         return Workstation.class;
     }
 }

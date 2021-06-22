package com.wojcik;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;


 class EmployeeService extends EntityService<Employee> {


     protected EmployeeService(RestHighLevelClient client, ObjectMapper objectMapper) {
         super(client, objectMapper);
     }

     @Override
     Class<Employee> getEntityType() {
         return Employee.class;
     }
 }

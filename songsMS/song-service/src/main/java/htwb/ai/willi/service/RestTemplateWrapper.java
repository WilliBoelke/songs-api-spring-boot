package htwb.ai.willi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public interface RestTemplateWrapper
{


     public String authenticateUser(String id);
}

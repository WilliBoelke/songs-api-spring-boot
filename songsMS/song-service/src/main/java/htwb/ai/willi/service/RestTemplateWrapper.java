package htwb.ai.willi.service;

import org.springframework.stereotype.Service;

@Service
public interface RestTemplateWrapper
{
     public String authenticateUser(String id);
}

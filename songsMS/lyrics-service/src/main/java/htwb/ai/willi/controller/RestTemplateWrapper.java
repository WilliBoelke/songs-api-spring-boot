package htwb.ai.willi.controller;

import org.springframework.stereotype.Service;

@Service
public interface RestTemplateWrapper
{
     public String authenticateUser(String authorization);
}

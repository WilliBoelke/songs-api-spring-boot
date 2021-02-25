package htwb.ai.willi.lyricsservice.controller;


import htwb.ai.willi.service.RestTemplateWrapper;
import htwb.ai.willi.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/lyrics")
@Slf4j
public class LyricController
{


     //-----------INSTANCE VARIABLES  -----------//

     @Autowired
     private LyricService lyricService;

     @Autowired
     private RestTemplateWrapper restTemplateWrapper;

     private Logger log = LoggerFactory.getLogger(LyricController.class);



     @PostMapping

}

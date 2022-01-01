package com.bitop.otcapi.fcg.controller;

import com.bitop.otcapi.fcg.entity.vo.Greeting;
import com.bitop.otcapi.fcg.entity.vo.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.util.HtmlUtils;

public class WSGreetingController {

    @MessageMapping("/sub/chat")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay

//        createCollectionService.createCollection();
//        createCollectionService.insertMessage(message.getName());

        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}

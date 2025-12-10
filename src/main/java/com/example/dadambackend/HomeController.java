package com.example.dadambackend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 컨트롤러가 스캔되는지 확인용
    @GetMapping("/test")
    public String test() {
        // 간단한 뷰로 연결하거나, 정말 텍스트로만 확인하고 싶으면 @ResponseBody 유지
        // 지금은 "ok"만 찍는 간단한 템플릿(view)로 연결한다고 가정
        return "test"; // templates/test.html (필요하면 만들기)
    }

    // 루트("/") 요청을 dadam/home.html 로 보내기
    @GetMapping("/")
    public String home() {
        // src/main/resources/templates/dadam/home.html
        return "dadam/home";
    }
}

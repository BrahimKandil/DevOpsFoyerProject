package tn.esprit.spring.schedular;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class Test {
//
    @Scheduled(fixedRate = 300000)
    void affiche(){
        log.info("Bonjour");
    }
}

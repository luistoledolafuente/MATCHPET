package com.matchpet.backend_user;

import com.matchpet.backend_user.service.Imp.RecomendacionServiceImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendUserApplicationTests {

    @MockBean
    private RecomendacionServiceImpl recomendacionService;

    @Test
    void contextLoads() {
    }

}
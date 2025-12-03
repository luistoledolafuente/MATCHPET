package com.matchpet.backend_user.services;

import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.repository.AnimalRepository;
import com.matchpet.backend_user.service.AnimalService;
import com.matchpet.backend_user.service.Imp.AnimalServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AnimalServiceMockitoTest {

    @InjectMocks
    private AnimalServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimalRepository animalRepository;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAnimalesByRefugio_CuandoRefugioExiste() {

        Refugio r = new Refugio();
        r.setId(1);

        UserModel u = new UserModel();
        u.setEmail("test@mail.com");
        u.setRefugio(r);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        service.getAnimalesByRefugio("test@mail.com");

        verify(animalRepository, times(1)).findByRefugio(r);
    }
}

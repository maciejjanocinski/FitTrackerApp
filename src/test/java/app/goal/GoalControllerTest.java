package app.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static app.utils.TestUtils.userNotFoundMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    GoalResponseDto goalResponseDto;

    @Mock
    GoalDto goalDto;

    @Test
    void getGoal_inputDataOk_returns200() throws Exception {
        //given
        when(goalService.getGoal(any())).thenReturn(goalResponseDto);

        //when
        mockMvc.perform(get("/goal/")
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(goalResponseDto)))
                .andDo(print());

        //then
        verify(goalService).getGoal(any());
    }

    @Test
    void getGoal_unauthorized_returns401() throws Exception {
        //when
        mockMvc.perform(get("/goal/"))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        //then
        verify(goalService, never()).getGoal(any());
    }

    @Test
    void getGoal_userNotFound_returns404() throws Exception {
        //given
        when(goalService.getGoal(any())).thenThrow(new UsernameNotFoundException(userNotFoundMessage));

        //when
        mockMvc.perform(get("/goal/")
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());

        //then
        verify(goalService).getGoal(any());
    }

    @Test
    void setGoal_returns200() throws Exception {
        //given
        when(goalService.setGoal(any(), any())).thenReturn(goalResponseDto);

        //when
        mockMvc.perform(post("/goal/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(goalResponseDto)))
                .andDo(print());

        //then
        verify(goalService).setGoal(any(), any());
    }

    @Test
    void setGoal_userNotFound_returns404() throws Exception {
        //given
        when(goalService.getGoal(any())).thenThrow(new UsernameNotFoundException(userNotFoundMessage));

        //when
        mockMvc.perform(get("/goal/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());

        //then
        verify(goalService).getGoal(any());
    }
}
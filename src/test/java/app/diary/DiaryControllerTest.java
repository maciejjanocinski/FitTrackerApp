package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
import app.exceptions.ProductNotFoundException;
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

import static app.utils.TestUtils.productNotFoundMessage;
import static app.utils.TestUtils.userNotFoundMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiaryService diaryService;

    @Mock
    private ProductInDiaryDto productInDiaryDto;

    @Mock
    private AddProductToDiaryDto addProductToDiaryDto;

    @Mock
    private EditProductInDiaryDto editProductInDiaryDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDiary_returns200() throws Exception {
        //given
        when(diaryService.getDiary(any())).thenReturn(mock(DiaryDto.class));

        //when
        mockMvc.perform(get("/diary/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(mock(DiaryDto.class))))
                .andDo(print());

        //then
        verify(diaryService).getDiary(any());
    }

    @Test
    void addProductToDiary_inputDataOk_returns200() throws Exception {
        //given
        when(diaryService.addProductToDiary(any(), any())).thenReturn(productInDiaryDto);
        //when
        mockMvc.perform(post("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(productInDiaryDto)))
                .andDo(print());

        //then
        verify(diaryService).addProductToDiary(any(), any());
    }

    @Test
    void addProductToDiary_productNotFound_returns404() throws Exception {
        //given
        when(diaryService.addProductToDiary(any(), any()))
                .thenThrow(new ProductNotFoundException(productNotFoundMessage));

        //when
        mockMvc.perform(post("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(productNotFoundMessage))
                .andDo(print());

        //then
        verify(diaryService).addProductToDiary(any(), any());
    }

    @Test
    void addProductToDiary_userNotFound_returns404() throws Exception {
        //given
        when(diaryService.addProductToDiary(any(), any()))
                .thenThrow(new UsernameNotFoundException(userNotFoundMessage));
        //when
        mockMvc.perform(post("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());

        //then
        verify(diaryService).addProductToDiary(any(), any());
    }

    @Test
    void editProductAmountInDiary_returns200() throws Exception {
        //given
        when(diaryService.editProductAmountInDiary(any(), any())).thenReturn(productInDiaryDto);

        //when
        mockMvc.perform(patch("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editProductInDiaryDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(productInDiaryDto)))
                .andDo(print());

        //then
        verify(diaryService).editProductAmountInDiary(any(), any());
    }

    @Test
    void editProductAmountInDiary_productNotFound_returns404() throws Exception {
        //given
        when(diaryService.editProductAmountInDiary(any(), any()))
                .thenThrow(new ProductNotFoundException(productNotFoundMessage));

        //when
        mockMvc.perform(patch("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editProductInDiaryDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(productNotFoundMessage))
                .andDo(print());

        //then
        verify(diaryService).editProductAmountInDiary(any(), any());
    }

    @Test
    void editProductAmountInDiary_userNotFound_returns404() throws Exception {
        //given
        when(diaryService.editProductAmountInDiary(any(), any()))
                .thenThrow(new UsernameNotFoundException(userNotFoundMessage));
        //when
        mockMvc.perform(patch("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editProductInDiaryDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());

        //then
        verify(diaryService).editProductAmountInDiary(any(), any());
    }

    @Test
    void deleteProductFromDiary_returns200() throws Exception {
        //given
        String message = "Product deleted from diary successfully";
        when(diaryService.deleteProductFromDiary(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(delete("/diary/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1L)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());
        //then
        verify(diaryService).deleteProductFromDiary(any(), any());
    }

    @Test
    void deleteProductFromDiary_productNotFound_returns404() throws Exception {
        //given
        when(diaryService.deleteProductFromDiary(any(), any()))
                .thenThrow(new ProductNotFoundException(productNotFoundMessage));

        //when
        mockMvc.perform(delete("/diary/product")
                        .content(objectMapper.writeValueAsString(1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(productNotFoundMessage))
                .andDo(print());

        //then
        verify(diaryService).deleteProductFromDiary(any(), any());
    }

    @Test
    void deleteProductFromDiary_userNotFound_returns404() throws Exception {
        //given
        when(diaryService.deleteProductFromDiary(any(), any()))
                .thenThrow(new UsernameNotFoundException(userNotFoundMessage));

        //when
        mockMvc.perform(delete("/diary/product")
                        .content(objectMapper.writeValueAsString(1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());
        //then
        verify(diaryService).deleteProductFromDiary(any(), any());
    }
}